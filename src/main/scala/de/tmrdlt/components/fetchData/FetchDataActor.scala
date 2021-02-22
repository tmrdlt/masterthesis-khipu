package de.tmrdlt.components.fetchData

import akka.actor.{Actor, ActorLogging, Props}
import de.tmrdlt.components.fetchData.FetchDataActor.{FetchDataGitHub, FetchDataTrello}
import de.tmrdlt.connectors.{GitHubApi, TrelloApi}
import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListDB}
import de.tmrdlt.models.WorkflowListState.getWorkflowListState
import de.tmrdlt.models.{TrelloAction, TrelloActionType, WorkflowListDataSource, WorkflowListState, WorkflowListType, WorkflowListUseCase}
import de.tmrdlt.utils.OptionExtensions

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FetchDataActor(trelloApi: TrelloApi,
                     gitHubApi: GitHubApi,
                     workflowListDB: WorkflowListDB) extends Actor with ActorLogging with OptionExtensions {

  override def receive: PartialFunction[Any, Unit] = {

    case FetchDataTrello(boardIds: Seq[String], now: LocalDateTime) =>

      val desiredActions = Seq(TrelloActionType.createBoard, TrelloActionType.createList, TrelloActionType.createCard)
      // To be able to keep and store the order of the lists retrieved by the API this Seq[Seq[Foo]] data structure is
      // used together with flatMap and zipWithIndex
      (for {
        boards <- Future.sequence(boardIds.map(b => trelloApi.getBoard(b)))
        listsLists <- Future.sequence(boardIds.map(b => trelloApi.getListOnABoard(b)))
        cardsLists <- Future.sequence(listsLists.flatten.map(l => trelloApi.getCardsInAList(l.id)))

        actionsOfBoards <- Future.sequence(boardIds.map(b => trelloApi.getActionsOfABoard(b))).map(_.flatten)
        actionsOfLists <- Future.sequence(listsLists.flatten.map(l => trelloApi.getActionsOfAList(l.id))).map(_.flatten)
        // actionsOfCards <- Future.sequence(cardsLists.flatten.map(c => trelloApi.getActionsOfAList(c.id))).map(_.flatten)

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
            createdAt = actionsOfBoards.find(a => a.`type` == TrelloActionType.createBoard && a.data.board.id == trelloBoard.id).map(_.date).getOrException("Error getting creation date for board"),
            updatedAt = actionsOfBoards.filter(a => a.data.board.id == trelloBoard.id).sorted(Ordering.by((_:TrelloAction).date).reverse).headOption.map(_.date).getOrException("Error getting update date for board")
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
            createdAt = actionsOfLists.filter(a => a.data.list.map(_.id).contains(trelloList.id)).sortBy(_.date).headOption.map(_.date).getOrException("Error getting creation date for list"),
            updatedAt = actionsOfLists.filter(a => a.data.list.map(_.id).contains(trelloList.id)).sorted(Ordering.by((_:TrelloAction).date).reverse).headOption.map(_.date).getOrException("Error getting update date for list")
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
            createdAt = actionsOfLists.find(a => a.`type` == TrelloActionType.createCard && a.data.card.map(_.id).contains(trelloCard.id)).map(_.date).getOrException("Error getting creation date for card"),
            updatedAt = trelloCard.dateLastActivity
          )
        }))

        // TODO use and store
        // insertedActions <- trelloDB.insertTrelloActions(actionsOfBoard.map(_.toTrelloActionDBEntity))
      } yield {
        val inserted = insertedBoards.length + insertedLists.length + insertedCards.length
        log.info(s"Fetching Trello data completed. Inserted a total of ${inserted} rows.")
      }).recoverWith {
        case t: Throwable => log.error(t, "error fetching trello data")
          Future.failed(t)
      }


    case FetchDataGitHub(orgNames, now) => {
      // To be able to keep and store the order of the lists retrieved by the API this Seq[Seq[Foo]] data structure is
      // used together with flatMap and zipWithIndex
      (for {
        projectsLists <- Future.sequence(orgNames.map(b => gitHubApi.getProjectsOfOrganisation(b)))
        columnsLists <- Future.sequence(projectsLists.flatten.map(p => gitHubApi.getColumnsOfProject(p.columns_url)))
        cardsLists <- Future.sequence(columnsLists.flatten.map(c => gitHubApi.getCardsOfColumn(c.cards_url)))
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

        insertedProjects <- workflowListDB.insertWorkflowLists(projectsLists.flatMap(_.zipWithIndex.map {
          case (gitHubProject, index) =>
            WorkflowList(
              id = 0L,
              apiId = gitHubProject.id.toString,
              title = gitHubProject.name,
              description = Some(gitHubProject.body),
              parentId = None,
              position = index.toLong, // For projects this doesn't seem to necessarily match the project order in the frontend but doesn't matter
              listType = WorkflowListType.BOARD,
              state = getWorkflowListState(gitHubProject.state),
              dataSource = WorkflowListDataSource.GitHub,
              useCase = Some(WorkflowListUseCase.softwareDevelopment), // TODO Add to request
              createdAt = gitHubProject.created_at,
              updatedAt = gitHubProject.updated_at
            )
        }))

        insertedColumns <- workflowListDB.insertWorkflowLists(columnsLists.flatMap(_.zipWithIndex.map {
          case (gitHubColumn, index) =>
            WorkflowList(
              id = 0L,
              apiId = gitHubColumn.id.toString,
              title = gitHubColumn.name,
              description = None,
              parentId = insertedProjects.find(_.apiId == gitHubColumn.project_url.split("/").last).map(_.id),
              position = index.toLong,
              listType = WorkflowListType.LIST,
              state = Some(WorkflowListState.OPEN), // Columns exist on GitHub only in the OPEN state
              dataSource = WorkflowListDataSource.GitHub,
              useCase = Some(WorkflowListUseCase.softwareDevelopment), // TODO Add to request
              createdAt = gitHubColumn.created_at,
              updatedAt = gitHubColumn.updated_at
            )
        }))

        insertedIssues <- workflowListDB.insertWorkflowLists(cardsAndIssuesLists.flatMap(_.zipWithIndex.map {
          case ((gitHubCard, Some(gitHubIssue)), index) =>
            WorkflowList(
              id = 0L,
              apiId = gitHubIssue.id.toString,
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
              apiId = gitHubCard.id.toString,
              title = gitHubCard.note.getOrException("Error taking note of card"),
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

        // TODO use and store
        // events <- Future.sequence(issues.map(i => gitHubApi.getEventsForIssue(i.events_url))).map(_.flatten)
      } yield {
        val inserted = insertedProjects.length + insertedColumns.length + insertedIssues.length
        log.info(s"Fetching GitHub data completed. Inserted a total of ${inserted} rows.")
      }).recoverWith {
        case t: Throwable => log.error(t, "error fetching github data")
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
            workflowListDB: WorkflowListDB): Props =
    Props(new FetchDataActor(trelloApi, gitHubApi, workflowListDB))

  val name = "FetchDataActor"

  case class FetchDataTrello(boardIds: Seq[String], now: LocalDateTime)

  case class FetchDataGitHub(orgNames: Seq[String], now: LocalDateTime)

}