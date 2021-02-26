package de.tmrdlt.components.fetchData

import akka.actor.{Actor, ActorLogging, Props}
import de.tmrdlt.components.fetchData.FetchDataActor.{FetchDataTrello, FetchGitHubDataForProject}
import de.tmrdlt.connectors.{GitHubApi, TrelloApi}
import de.tmrdlt.database.action.{Action, ActionDB}
import de.tmrdlt.database.github.GitHubDB
import de.tmrdlt.database.moveaction.MoveAction
import de.tmrdlt.database.trello.TrelloDB
import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListDB}
import de.tmrdlt.models.WorkflowListState.getWorkflowListState
import de.tmrdlt.models._
import de.tmrdlt.utils.{DateUtil, FutureUtil, OptionExtensions}

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FetchDataActor(trelloApi: TrelloApi,
                     gitHubApi: GitHubApi,
                     workflowListDB: WorkflowListDB,
                     actionDB: ActionDB,
                     trelloDB: TrelloDB,
                     gitHubDB: GitHubDB) extends Actor with ActorLogging with OptionExtensions {

  override def receive: PartialFunction[Any, Unit] = {

    case FetchDataTrello(boardIds: Seq[String], now: LocalDateTime) =>

      val desiredActions = Seq(TrelloActionType.createBoard, TrelloActionType.createList, TrelloActionType.createCard)
      // To be able to keep and store the order of the lists retrieved by the API this Seq[Seq[Foo]] data structure is
      // used together with flatMap and zipWithIndex
      (for {
        boards <- Future.sequence(boardIds.map(b => trelloApi.getBoard(b)))
        listsLists <- Future.sequence(boardIds.map(b => trelloApi.getListsOfBoard(b)))
        cardsLists <- Future.sequence(listsLists.flatten.map(l => trelloApi.getAllCardsOfList(l.id)))
        actionsOfBoards <- Future.sequence(boardIds.map(b => trelloApi.getAllActionsOfBoard(b))).map(_.flatten)

        insertedBoards <- workflowListDB.insertWorkflowLists(boards.map { trelloBoard =>
          WorkflowList(
            id = 0L,
            apiId = trelloBoard.id,
            title = trelloBoard.name,
            description = Some(trelloBoard.desc),
            parentId = None,
            position = 0,
            listType = WorkflowListType.BOARD,
            state = Some(getWorkflowListState(trelloBoard.closed)),
            dataSource = WorkflowListDataSource.Trello,
            useCase = None, // TODO Add to request
            createdAt = DateUtil.getDateFromObjectIdString(trelloBoard.id),
            updatedAt = actionsOfBoards
              .filter(a => a.data.board.id == trelloBoard.id)
              .sortBy(_.date)
              .lastOption.map(_.date)
              .getOrException("Error getting update date for board")
          )
        })
        insertedLists <- workflowListDB.insertWorkflowLists(listsLists.flatMap(_.zipWithIndex.map {
          case (trelloList, index) =>
            WorkflowList(
              id = 0L,
              apiId = trelloList.id,
              title = trelloList.name,
              description = None,
              parentId = insertedBoards.find(_.apiId == trelloList.idBoard).map(_.id),
              position = index.toLong,
              listType = WorkflowListType.LIST,
              state = Some(getWorkflowListState(trelloList.closed)),
              dataSource = WorkflowListDataSource.Trello,
              useCase = None, // TODO Add to request
              createdAt = DateUtil.getDateFromObjectIdString(trelloList.id),
              updatedAt = actionsOfBoards
                .filter(a => a.data.list.map(_.id).contains(trelloList.id))
                .sortBy(_.date)
                .lastOption.map(_.date)
                .getOrException("Error getting update date for list")
            )
        }))
        insertedCards <- workflowListDB.insertWorkflowLists(cardsLists.flatMap(_.zipWithIndex.map {
          case (trelloCard, index) =>
            WorkflowList(
              id = 0L,
              apiId = trelloCard.id,
              title = trelloCard.name,
              description = Some(trelloCard.desc),
              parentId = insertedLists.find(_.apiId == trelloCard.idList).map(_.id),
              position = index.toLong,
              listType = WorkflowListType.ITEM,
              state = Some(getWorkflowListState(trelloCard.closed)),
              dataSource = WorkflowListDataSource.Trello,
              useCase = None, // TODO Add to request
              createdAt = DateUtil.getDateFromObjectIdString(trelloCard.id),
              updatedAt = trelloCard.dateLastActivity
            )
        }))

        insertedActions <- actionDB.insertActions(actionsOfBoards.map { trelloAction =>
          Action(
            id = 0L,
            apiId = trelloAction.id,
            actionType = trelloAction.`type`.toString,
            workflowListApiId = (trelloAction.data.card, trelloAction.data.list) match {
              case (Some(card), _) => card.id
              case (_, Some(list)) => list.id
              case (_, _) => trelloAction.data.board.id
            },
            userApiId = trelloAction.idMemberCreator,
            date = trelloAction.date,
            dataSource = WorkflowListDataSource.Trello
          )
        })

        insertedMoveActions <- actionDB.insertMoveActions(
          actionsOfBoards
            .filter(_.isMoveCardToNewColumnAction)
            .map { trelloAction =>
              MoveAction(
                id = 0L,
                actionId = insertedActions.find(_.apiId == trelloAction.id)
                  .getOrException(s"Could not find apiId ${trelloAction.id} in inserted Actions").id,
                oldListApiId = trelloAction.data.listBefore.getOrException("Could not get oldListApiId").id,
                newListApiId = trelloAction.data.listAfter.getOrException("Could not get oldListApiId").id
              )
            })
      } yield {
        val inserted = insertedBoards.length + insertedLists.length + insertedCards.length + insertedActions.length
        log.info(s"Fetching Trello data completed. Inserted a total of ${inserted} rows.")
      }).recoverWith {
        case t: Throwable => log.error(t, "error fetching trello data")
          Future.failed(t)
      }


    case FetchGitHubDataForProject(gitHubProject, position) => {

      log.info(s"Start fetching GitHub data for project '${gitHubProject.name}'.")

      // To be able to keep and store the order of the lists retrieved by the API this Seq[Seq[Foo]] data structure is
      // used together with flatMap and zipWithIndex
      (for {
        columns <- gitHubApi.getColumnsOfProject(gitHubProject.columns_url)
        cardsLists <- Future.sequence(columns.map(c => gitHubApi.getAllCardsOfColumn(c.cards_url)))
        cardsAndIssuesLists <- Future.sequence(
          cardsLists
            .map { cl =>
              Future.sequence(
                cl.map { card =>
                  (card.note, card.content_url) match {
                    case (Some(_), None) => Future.successful(card, None) // Card is a note
                    case (None, Some(content_url)) => gitHubApi.getContentOfNote(content_url).map(i => (card, Some(i))) // Card is an issue
                    case _ => Future.failed(new Exception("Error fetching github data. Card was neither an issue nor a note"))
                  }
                }
              )
            }
        )
        cardsAndIssueAndEvents <- Future.sequence(
          cardsAndIssuesLists
            .flatten
            .map { case (card, Some(issue)) =>
              gitHubApi.getAllEventsOfIssue(issue.events_url)
                .map(_.map(event =>
                  (card, issue, event)
                ))
            case _ => Future.successful(Seq.empty)
            }
        )

        insertedProject <- workflowListDB.insertWorkflowList(
          WorkflowList(
            id = 0L,
            apiId = gitHubProject.id.toString,
            title = gitHubProject.name,
            description = Some(gitHubProject.body),
            parentId = None,
            position = position.toLong, // For projects this doesn't seem to necessarily match the project order in the frontend but doesn't matter
            listType = WorkflowListType.BOARD,
            state = getWorkflowListState(gitHubProject.state),
            dataSource = WorkflowListDataSource.GitHub,
            useCase = Some(WorkflowListUseCase.softwareDevelopment), // TODO Add to request
            createdAt = gitHubProject.created_at,
            updatedAt = gitHubProject.updated_at
          )
        )

        insertedColumns <- workflowListDB.insertWorkflowLists(columns.zipWithIndex.map {
          case (gitHubColumn, index) =>
            WorkflowList(
              id = 0L,
              apiId = gitHubColumn.id.toString,
              title = gitHubColumn.name,
              description = None,
              parentId = Some(insertedProject.id),
              position = index.toLong,
              listType = WorkflowListType.LIST,
              state = Some(WorkflowListState.OPEN), // Columns exist on GitHub only in the OPEN state
              dataSource = WorkflowListDataSource.GitHub,
              useCase = Some(WorkflowListUseCase.softwareDevelopment), // TODO Add to request
              createdAt = gitHubColumn.created_at,
              updatedAt = gitHubColumn.updated_at
            )
        })

        insertedCards <- workflowListDB.insertWorkflowLists(cardsAndIssuesLists.flatMap(_.zipWithIndex.map {
          case ((gitHubCard, Some(gitHubIssue)), index) =>
            WorkflowList(
              id = 0L,
              apiId = gitHubCard.id.toString, // always take card Id as events also refer to this
              title = gitHubIssue.title,
              description = gitHubIssue.body,
              parentId = insertedColumns.find(_.apiId == gitHubCard.column_url.split("/").last).map(_.id),
              position = index.toLong,
              listType = WorkflowListType.ITEM,
              state = getWorkflowListState(gitHubIssue.state),
              dataSource = WorkflowListDataSource.GitHub,
              useCase = Some(WorkflowListUseCase.softwareDevelopment), // TODO Add to request
              createdAt = gitHubIssue.created_at,
              updatedAt = gitHubIssue.updated_at
            )
          case ((gitHubCard, None), index) =>
            WorkflowList(
              id = 0L,
              apiId = gitHubCard.id.toString, // always take card Id as actions also refer to this
              title = gitHubCard.note.getOrException("Couldn't get note of card"),
              description = None,
              parentId = insertedColumns.find(_.apiId == gitHubCard.column_url.split("/").last).map(_.id),
              position = index.toLong,
              listType = WorkflowListType.ITEM,
              state = Some(WorkflowListState.OPEN), // Notes exist on GitHub only in the OPEN state
              dataSource = WorkflowListDataSource.GitHub,
              useCase = Some(WorkflowListUseCase.softwareDevelopment), // TODO Add to request
              createdAt = gitHubCard.created_at,
              updatedAt = gitHubCard.updated_at
            )
        }))


        insertedEvents <- actionDB.insertActions(cardsAndIssueAndEvents.flatten.map {
          case (gitHubCard, gitHubIssue, gitHubIssueEvent) =>
            Action(
              id = 0L,
              apiId = gitHubIssueEvent.id.toString,
              actionType = gitHubIssueEvent.event.toString,
              workflowListApiId = gitHubCard.id.toString,
              userApiId = gitHubIssueEvent.actor.id.toString,
              date = gitHubIssueEvent.created_at,
              dataSource = WorkflowListDataSource.GitHub
            )
        })

        moveEvents = cardsAndIssueAndEvents.flatten.filter { case (_, _, e) => e.event == GitHubIssueEventType.moved_columns_in_project }
        insertedMoveEvents <- actionDB.insertMoveActions(moveEvents.map {
          case (gitHubCard, gitHubIssue, gitHubIssueEvent) =>
            val projectCard = gitHubIssueEvent.project_card.getOrException(s"Could not get project_card fpr ${gitHubIssueEvent.id}")
            MoveAction(
              id = 0L,
              actionId = insertedEvents.find(_.apiId == gitHubIssueEvent.id.toString)
                .getOrException(s"Could not find apiId ${gitHubIssueEvent.id} in inserted Actions").id,
              oldListApiId = insertedColumns
                .find(wl => wl.title == projectCard.previous_column_name.getOrException(s"Could not get previous_column_name for ${gitHubIssueEvent.id}"))
                .getOrException(s"Could not find oldListApiId for ${gitHubIssueEvent.id}").apiId,
              newListApiId = insertedColumns
                .find(wl => wl.title == projectCard.column_name.getOrException("Could not get old column name"))
                .getOrException(s"Could not find newListApiId for ${gitHubIssueEvent.id}").apiId
            )
        })
      } yield {
        val inserted = 1 + insertedColumns.length + insertedCards.length + insertedMoveEvents.length
        log.info(s"Completed fetching GitHub data for project '${gitHubProject.name}'. Inserted a total of ${inserted} rows.")
      }).recoverWith {
        case t: Throwable => log.error(t, s"Error fetching GitHub data for project '${gitHubProject.name}''")
          Future.failed(t)
      }

    }


      // https://docs.github.com/en/developers/webhooks-and-events/issue-event-types
      def isRelevantEvent(event: String): Boolean = {
        event match {
          case "added_to_project" => true
          case "moved_columns_in_project" => true
          case "removed_from_project" => true
          case "renamed" => true
          case _ => false
        }
      }
  }
}


object FetchDataActor {

  def props(trelloApi: TrelloApi,
            gitHubApi: GitHubApi,
            workflowListDB: WorkflowListDB,
            actionDB: ActionDB,
            trelloDB: TrelloDB,
            gitHubDB: GitHubDB): Props =
    Props(new FetchDataActor(trelloApi, gitHubApi, workflowListDB, actionDB, trelloDB, gitHubDB))

  val name = "FetchDataActor"

  case class FetchDataTrello(boardIds: Seq[String], now: LocalDateTime)

  case class FetchGitHubDataForProject(gitHubProject: GitHubProject, position: Int)

}