package de.tmrdlt.components.fetchData

import akka.actor.{Actor, ActorLogging, Props}
import de.tmrdlt.components.fetchData.FetchDataActor.{FetchDataTrello, FetchGitHubDataForProject}
import de.tmrdlt.connectors.{GitHubApi, TrelloApi}
import de.tmrdlt.database.action.{Action, ActionDB}
import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListDB}
import de.tmrdlt.models.WorkflowListState.getWorkflowListState
import de.tmrdlt.models._
import de.tmrdlt.utils.{CsvUtil, DateUtil, FutureUtil, OptionExtensions}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FetchDataActor(trelloApi: TrelloApi,
                     gitHubApi: GitHubApi,
                     workflowListDB: WorkflowListDB,
                     actionDB: ActionDB) extends Actor with ActorLogging with OptionExtensions {

  override def receive: PartialFunction[Any, Unit] = {

    case FetchDataTrello(boardIds: Seq[String]) =>

      val desiredActions = Seq(TrelloActionType.createBoard, TrelloActionType.createList, TrelloActionType.createCard)

      def fetchDataForTrelloBoard(boardId: String) = {
        log.info(s"Start fetching Trello data for boardId $boardId...")
        (for {
          board <- trelloApi.getBoard(boardId)
          lists <- trelloApi.getListsOfBoard(boardId)
          cards <- trelloApi.getAllCardsOfBoard(boardId)
          actions <- trelloApi.getAllActionsOfBoard(boardId)

          insertedBoard <- workflowListDB.insertWorkflowList(
            WorkflowList(
              id = 0L,
              apiId = board.id,
              title = board.name,
              description = Some(board.desc),
              parentId = None,
              position = 0,
              listType = WorkflowListType.BOARD,
              state = Some(getWorkflowListState(board.closed)),
              dataSource = WorkflowListDataSource.Trello,
              useCase = None, // TODO Add to request
              createdAt = DateUtil.getDateFromObjectIdString(board.id),
              updatedAt = actions
                .filter(a => a.data.board.id == board.id)
                .sortBy(_.date)
                .lastOption.map(_.date)
                .getOrException("Error getting update date for board")
            )
          )
          // To be able to keep and store the order of the lists retrieved by the API we use .zipWithIndex.map
          insertedLists <- workflowListDB.insertWorkflowLists(lists.zipWithIndex.map {
            case (trelloList, index) =>
              WorkflowList(
                id = 0L,
                apiId = trelloList.id,
                title = trelloList.name,
                description = None,
                parentId = Some(insertedBoard.id),
                position = index.toLong,
                listType = WorkflowListType.LIST,
                state = Some(getWorkflowListState(trelloList.closed)),
                dataSource = WorkflowListDataSource.Trello,
                useCase = None, // TODO Add to request
                createdAt = DateUtil.getDateFromObjectIdString(trelloList.id),
                updatedAt = actions
                  .filter(a => a.data.list.map(_.getId).contains(trelloList.id))
                  .sortBy(_.date)
                  .lastOption.map(_.date)
                  .getOrException("Error getting update date for list")
              )
          })
          insertedCards <- workflowListDB.insertWorkflowLists(cards.zipWithIndex.map {
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
          })
          insertedActions <- actionDB.insertActions(actions.map { trelloAction =>
            Action(
              id = 0L,
              apiId = trelloAction.id,
              actionType = trelloAction.`type`.toString,
              workflowListApiId = (trelloAction.data.card, trelloAction.data.list) match {
                case (Some(card), _) => card.id
                case (_, Some(list)) => list.getId
                case (_, _) => trelloAction.data.board.id
              },
              boardApiId = Some(insertedBoard.apiId),
              parentApiId =
                if (trelloAction.isCreateOrDeleteAction)
                  Some(trelloAction.data.list.getOrException("Could not get parentApiId").getId)
                else None,
              oldParentApiId =
                if (trelloAction.isMoveCardToNewColumnAction) Some(trelloAction.data.listBefore.getOrException("Could not get oldListApiId").getId)
                else None,
              newParentApiId =
                if (trelloAction.isMoveCardToNewColumnAction) Some(trelloAction.data.listAfter.getOrException("Could not get oldListApiId").getId)
                else None,
              userApiId = trelloAction.idMemberCreator,
              date = trelloAction.date,
              dataSource = WorkflowListDataSource.Trello
            )
          })

          _ <- CsvUtil.writeWorkflowListsToCsv(insertedBoard.title, Seq(insertedBoard) ++ insertedLists ++ insertedCards)
          _ <- CsvUtil.writeActionsToCsv(insertedBoard.title, insertedActions)

        } yield {
          val inserted = 1 + insertedLists.length + insertedCards.length + insertedActions.length
          log.info(s"Fetching Trello for Board ${insertedBoard.title} data completed. Inserted a total of ${inserted} rows.")
        }).recoverWith {
          case t: Throwable => log.error(t, "error fetching trello data")
            Future.failed(t)
        }
      }

      FutureUtil.linearize(boardIds)(fetchDataForTrelloBoard)


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
            val projectCard = gitHubIssueEvent.project_card.getOrException(s"Could not get project_card fpr ${gitHubIssueEvent.id}")
            Action(
              id = 0L,
              apiId = gitHubIssueEvent.id.toString,
              actionType = gitHubIssueEvent.event.toString,
              workflowListApiId = gitHubCard.id.toString,
              boardApiId = Some(insertedProject.apiId),
              parentApiId = None, // TODO check
              oldParentApiId =
                if (gitHubIssueEvent.isMoveToNewColumnEvent)
                  Some(
                    insertedColumns
                      .find(wl => wl.title == projectCard.previous_column_name.getOrException(s"Could not get previous_column_name"))
                      .getOrException(s"Could not find oldParentApiId for ${gitHubIssueEvent.id}").apiId
                  )
                else None,
              newParentApiId =
                if (gitHubIssueEvent.isMoveToNewColumnEvent)
                  Some(
                    insertedColumns
                      .find(wl => wl.title == projectCard.column_name.getOrException("Could not current new column_name"))
                      .getOrException(s"Could not find newParentApiId for ${gitHubIssueEvent.id}").apiId
                  )
                else None,
              userApiId = gitHubIssueEvent.actor.id.toString,
              date = gitHubIssueEvent.created_at,
              dataSource = WorkflowListDataSource.GitHub
            )
        })

        _ <- CsvUtil.writeWorkflowListsToCsv(insertedProject.title, Seq(insertedProject) ++ insertedColumns ++ insertedCards)
        _ <- CsvUtil.writeActionsToCsv(insertedProject.title, insertedEvents)

      } yield {
        val inserted = 1 + insertedColumns.length + insertedCards.length + insertedEvents.length
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
            actionDB: ActionDB): Props =
    Props(new FetchDataActor(trelloApi, gitHubApi, workflowListDB, actionDB))

  val name = "FetchDataActor"

  case class FetchDataTrello(boardIds: Seq[String])

  case class FetchGitHubDataForProject(gitHubProject: GitHubProject, position: Int)

}