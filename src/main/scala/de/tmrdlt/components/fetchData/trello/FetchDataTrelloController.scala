package de.tmrdlt.components.fetchData.trello

import akka.actor.ActorRef
import de.tmrdlt.components.fetchData.trello.FetchDataTrelloActor.FetchDataTrello
import de.tmrdlt.connectors.TrelloApi
import de.tmrdlt.database.trello.TrelloDB
import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListDB}
import de.tmrdlt.models.WorkflowListState.WorkflowListState
import de.tmrdlt.models.{WorkflowListDataSource, WorkflowListState, WorkflowListType}
import de.tmrdlt.utils.SimpleNameLogger

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FetchDataTrelloController(trelloApi: TrelloApi,
                                trelloDB: TrelloDB,
                                workflowListDB: WorkflowListDB,
                                fetchDataTrelloActor: ActorRef)
  extends SimpleNameLogger {

  def fetchDataTrello(boardIds: Seq[String]): Future[Int] = {
    // Defining the futures before the for yield makes them run in parallel
    // val boardFuture = Future.sequence(boardIds.map(b => trelloApi.getBoard(b)))
    // val listsOfBoardFuture = Future.sequence(boardIds.map(b => trelloApi.getListOnABoard(b))).map(_.flatten)
    // val actionsOfBoardFuture = Future.sequence(boardIds.map(b => trelloApi.getActionsOfABoard(b))).map(_.flatten)

    val now = LocalDateTime.now()

    for {
      boards <- Future.sequence(boardIds.map(b => trelloApi.getBoard(b)))
      listsOfBoard <- Future.sequence(boardIds.map(b => trelloApi.getListOnABoard(b))).map(_.flatten)
      actionsOfBoard <- Future.sequence(boardIds.map(b => trelloApi.getActionsOfABoard(b))).map(_.flatten)
      cardsOfBoard <- Future.sequence(listsOfBoard.map(l => trelloApi.getCardsInAList(l.id))).map(_.flatten)
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
          createdAt = now,
          updatedAt = now
        )
      })
      insertedLists <- workflowListDB.insertWorkflowLists(listsOfBoard.map { trelloList =>
        WorkflowList(
          id = 0L,
          apiId = trelloList.id,
          title = trelloList.name,
          description = None,
          parentId = insertedBoards.find(_.apiId == trelloList.idBoard).map(_.id),
          position = trelloList.pos,
          listType = WorkflowListType.LIST,
          state = Some(getWorkflowListState(trelloList.closed)),
          dataSource = WorkflowListDataSource.Trello,
          useCase = None, // TODO Add to request
          createdAt = now,
          updatedAt = now
        )
      })
      insertedCards <- workflowListDB.insertWorkflowLists(cardsOfBoard.map { trelloCard =>
        WorkflowList(
          id = 0L,
          apiId = trelloCard.id,
          title = trelloCard.name,
          description = Some(trelloCard.desc),
          parentId = insertedLists.find(_.apiId == trelloCard.idList).map(_.id),
          position = trelloCard.pos,
          listType = WorkflowListType.ITEM,
          state = Some(getWorkflowListState(trelloCard.closed)),
          dataSource = WorkflowListDataSource.Trello,
          useCase = None, // TODO Add to request
          createdAt = now,
          updatedAt = now
        )
      })
      // insertedActions <- trelloDB.insertTrelloActions(actionsOfBoard.map(_.toTrelloActionDBEntity))
    } yield {
      insertedBoards.length + insertedLists.length + insertedCards.length
    }
  }

  private def getWorkflowListState(closed: Boolean): WorkflowListState = {
    if (closed) WorkflowListState.CLOSED else WorkflowListState.OPEN
  }
}
