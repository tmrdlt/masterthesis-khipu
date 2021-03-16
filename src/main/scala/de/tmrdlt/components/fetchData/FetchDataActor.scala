package de.tmrdlt.components.fetchData

import akka.actor.{Actor, ActorLogging, Props}
import de.tmrdlt.components.fetchData.FetchDataActor.{FetchDataTrello, FetchDataGitHub}
import de.tmrdlt.connectors.{GitHubApi, TrelloApi}
import de.tmrdlt.database.action.{Event, EventDB}
import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListDB}
import de.tmrdlt.models.WorkflowListState.getWorkflowListState
import de.tmrdlt.models._
import de.tmrdlt.utils.{CsvUtil, DateUtil, FutureUtil, OptionExtensions}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FetchDataActor(trelloApi: TrelloApi,
                     gitHubApi: GitHubApi,
                     workflowListDB: WorkflowListDB,
                     eventDB: EventDB) extends Actor with ActorLogging with OptionExtensions {

  override def receive: PartialFunction[Any, Unit] = {

    case FetchDataTrello(boardIds: Seq[String]) => {
      log.info(s"Start fetching Trello data...")

      FutureUtil.linearize(boardIds)(fetchDataForTrelloBoard)
        .onComplete { res =>
          log.info(s"Completed fetching Trello data.")
          res
        }

      def fetchDataForTrelloBoard(boardId: String): Future[Boolean] = {
        log.info(s"Start fetching Trello data for board with ID '$boardId'.")
        (for {
          trelloBoard <- trelloApi.getBoard(boardId)
          trelloLists <- trelloApi.getListsOfBoard(boardId)
          trelloCards <- trelloApi.getAllCardsOfBoard(boardId)
          trelloActions <- trelloApi.getAllActionsOfBoard(boardId)

          insertedBoard <- workflowListDB.insertWorkflowList(
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
              updatedAt = trelloActions
                .filter(a => a.data.board.id == trelloBoard.id)
                .sortBy(_.date)
                .lastOption.map(_.date)
                .getOrException("Error getting update date for board")
            )
          )
          // To be able to keep and store the order of the lists retrieved by the API we use .zipWithIndex.map
          insertedLists <- workflowListDB.insertWorkflowLists(trelloLists.zipWithIndex.map {
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
                updatedAt = trelloActions
                  .filter(a => a.data.list.map(_.getId).contains(trelloList.id))
                  .sortBy(_.date)
                  .lastOption.map(_.date)
                  .getOrException("Error getting update date for list")
              )
          })
          insertedItems <- workflowListDB.insertWorkflowLists(trelloCards.zipWithIndex.map {
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
          insertedEvents <- eventDB.insertEvents(trelloActions.map { trelloAction =>
            Event(
              id = 0L,
              apiId = trelloAction.id,
              eventType = trelloAction.`type`.toString,
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

          // TODO check again when needed
          //_ <- CsvUtil.writeWorkflowListsToCsv(insertedBoard.title, Seq(insertedBoard) ++ insertedLists ++ insertedCards)
          //_ <- CsvUtil.writeEventsToCsv(insertedBoard.title, insertedEvents)

        } yield {
          val inserted = 1 + insertedLists.length + insertedItems.length + insertedEvents.length
          log.info(s"Fetching Trello for Board ${insertedBoard.title} data completed. Inserted a total of ${inserted} rows.")
          true
        }).recoverWith {
          case t: Throwable => log.error(t, "error fetching trello data")
            Future.successful(true)
        }
      }
    }

    case FetchDataGitHub(ownerAndRepo: String) => {
      log.info("Start fetching GitHub data...")

      (for {
        gitHubProjects <- gitHubApi.getProjectsOfRepo(ownerAndRepo)
        gitHubIssueEvents <- gitHubApi.getAllIssueEventsOfRepo(ownerAndRepo)
      } yield {
        gitHubProjects.zipWithIndex.map { gitHupProjectWithIndex =>
          fetchDataForGitHubProject(gitHupProjectWithIndex, gitHubIssueEvents)
        }
      }).recoverWith {
        case t: Throwable => log.error(t, s"Error fetching GitHub data")
          Future.successful(true)
      }.onComplete { res =>
        log.info(s"Completed fetching GitHub data.")
        res
      }

      def fetchDataForGitHubProject(gitHubProjectWithIndex: (GitHubProject, Int),
                                    issueEvents: Seq[GitHubIssueEvent]): Future[Boolean] = {
        val gitHubProject = gitHubProjectWithIndex._1
        val position = gitHubProjectWithIndex._2
        log.info(s"Start fetching GitHub data for project '${gitHubProject.name}'.")

        // To be able to keep and store the order of the lists retrieved by the API this Seq[Seq[Foo]] data structure is
        // used together with flatMap and zipWithIndex
        (for {
          gitHubColumns <- gitHubApi.getColumnsOfProject(gitHubProject.columns_url)
          gitHubCardsLists <- Future.sequence(gitHubColumns.map(column => gitHubApi.getAllCardsOfColumn(column.cards_url)))

          insertedBoard <- workflowListDB.insertWorkflowList(
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

          insertedLists <- workflowListDB.insertWorkflowLists(gitHubColumns.zipWithIndex.map {
            case (gitHubColumn, index) =>
              WorkflowList(
                id = 0L,
                apiId = gitHubColumn.id.toString,
                title = gitHubColumn.name,
                description = None,
                parentId = Some(insertedBoard.id),
                position = index.toLong,
                listType = WorkflowListType.LIST,
                state = Some(WorkflowListState.OPEN), // Columns exist on GitHub only in the OPEN state
                dataSource = WorkflowListDataSource.GitHub,
                useCase = Some(WorkflowListUseCase.softwareDevelopment), // TODO Add to request
                createdAt = gitHubColumn.created_at,
                updatedAt = gitHubColumn.updated_at
              )
          })

          insertedItems <- workflowListDB.insertWorkflowLists(gitHubCardsLists.flatMap(_.zipWithIndex.map {
            case (gitHubCard, index) =>
              gitHubCard.content_url match {
                // GitHubCard is issue
                case Some(content_url) =>
                  val gitHubIssue = issueEvents
                    .map(_.issue)
                    .collect{ case Some(issue) => issue}
                    .find(_.url == content_url)
                    .getOrException(new Exception("No issue event for content_url found"))
                  WorkflowList(
                    id = 0L,
                    apiId = gitHubCard.id.toString, // always take card Id as events also refer to this
                    title = gitHubIssue.title,
                    description = gitHubIssue.body,
                    parentId = insertedLists.find(_.apiId == gitHubCard.column_url.split("/").last).map(_.id),
                    position = index.toLong,
                    listType = WorkflowListType.ITEM,
                    state = getWorkflowListState(gitHubIssue.state),
                    dataSource = WorkflowListDataSource.GitHub,
                    useCase = Some(WorkflowListUseCase.softwareDevelopment), // TODO Add to request
                    createdAt = gitHubIssue.created_at,
                    updatedAt = gitHubIssue.updated_at
                  )
                // GitHubCard is card
                case None =>
                  WorkflowList(
                    id = 0L,
                    apiId = gitHubCard.id.toString, // always take card Id as actions also refer to this
                    title = gitHubCard.note.getOrException("Couldn't get note of card"),
                    description = None,
                    parentId = insertedLists.find(_.apiId == gitHubCard.column_url.split("/").last).map(_.id),
                    position = index.toLong,
                    listType = WorkflowListType.ITEM,
                    state = Some(WorkflowListState.OPEN), // Notes exist on GitHub only in the OPEN state
                    dataSource = WorkflowListDataSource.GitHub,
                    useCase = Some(WorkflowListUseCase.softwareDevelopment), // TODO Add to request
                    createdAt = gitHubCard.created_at,
                    updatedAt = gitHubCard.updated_at
                  )
              }
          }))

          insertedEvents <- eventDB.insertEvents(issueEvents.filter(_.project_card.isDefined).map { gitHubIssueEvent =>
            val projectCard = gitHubIssueEvent.project_card.getOrException(s"Could not get project_card for ${gitHubIssueEvent.id}")
            Event(
              id = 0L,
              apiId = gitHubIssueEvent.id.toString,
              eventType = gitHubIssueEvent.event,
              workflowListApiId = projectCard.id.toString,
              boardApiId = Some(insertedBoard.apiId),
              parentApiId =
                if (gitHubIssueEvent.isAddedOrRemovedEvent)
                  Some(
                    insertedLists
                      .find(wl => wl.title == projectCard.column_name.getOrException(s"Could not get column_name"))
                      .getOrException(s"Could not find parentApiId for ${gitHubIssueEvent.id}").apiId
                  )
                else None,
              oldParentApiId =
                if (gitHubIssueEvent.isMoveToNewColumnEvent)
                  Some(
                    insertedLists
                      .find(wl => wl.title == projectCard.previous_column_name.getOrException(s"Could not get previous_column_name"))
                      .getOrException(s"Could not find oldParentApiId for ${gitHubIssueEvent.id}").apiId
                  )
                else None,
              newParentApiId =
                if (gitHubIssueEvent.isMoveToNewColumnEvent)
                  Some(
                    insertedLists
                      .find(wl => wl.title == projectCard.column_name.getOrException("Could not get new column_name"))
                      .getOrException(s"Could not find newParentApiId for ${gitHubIssueEvent.id}").apiId
                  )
                else None,
              userApiId = gitHubIssueEvent.actor.map(_.id).getOrElse("0").toString,
              date = gitHubIssueEvent.created_at,
              dataSource = WorkflowListDataSource.GitHub
            )
          })

          //_ <- CsvUtil.writeWorkflowListsToCsv(insertedProject.title, Seq(insertedProject) ++ insertedColumns ++ insertedCards)
          //_ <- CsvUtil.writeEventsToCsv(insertedProject.title, insertedEvents)

        } yield {
          val inserted = 1 + insertedLists.length + insertedItems.length + insertedEvents.length
          log.info(s"Completed fetching GitHub data for project '${gitHubProject.name}'. Inserted a total of ${inserted} rows.")
          true
        }).recoverWith {
          case t: Throwable => log.error(t, s"Error fetching GitHub data for project '${gitHubProject.name}'")
            Future.successful(true)
        }

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
            eventDB: EventDB): Props =
    Props(new FetchDataActor(trelloApi, gitHubApi, workflowListDB, eventDB))

  val name = "FetchDataActor"

  case class FetchDataTrello(boardIds: Seq[String])

  case class FetchDataGitHub(ownerAndRepo: String)

}