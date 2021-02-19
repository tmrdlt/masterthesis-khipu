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

  def fetchDataTrello(boardIds: Seq[String]): Unit = {
    // Defining the futures before the for yield makes them run in parallel
    // val boardFuture = Future.sequence(boardIds.map(b => trelloApi.getBoard(b)))
    // val listsOfBoardFuture = Future.sequence(boardIds.map(b => trelloApi.getListOnABoard(b))).map(_.flatten)
    // val actionsOfBoardFuture = Future.sequence(boardIds.map(b => trelloApi.getActionsOfABoard(b))).map(_.flatten)

    fetchDataTrelloActor ! FetchDataTrello(boardIds)
  }
}
