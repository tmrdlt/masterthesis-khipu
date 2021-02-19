package de.tmrdlt.components.fetchData.trello

import akka.actor.ActorRef
import de.tmrdlt.components.fetchData.FetchDataActor.FetchDataTrello
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
                                fetchDataActor: ActorRef)
  extends SimpleNameLogger {

  def fetchDataTrello(boardIds: Seq[String]): Unit = {
    fetchDataActor ! FetchDataTrello(boardIds)
  }
}
