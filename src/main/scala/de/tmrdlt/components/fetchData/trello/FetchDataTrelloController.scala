package de.tmrdlt.components.fetchData.trello

import akka.actor.ActorRef
import de.tmrdlt.components.fetchData.FetchDataActor.FetchDataTrello
import de.tmrdlt.connectors.TrelloApi
import de.tmrdlt.database.trello.TrelloDB
import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.utils.SimpleNameLogger

import java.time.LocalDateTime


class FetchDataTrelloController(trelloApi: TrelloApi,
                                trelloDB: TrelloDB,
                                workflowListDB: WorkflowListDB,
                                fetchDataActor: ActorRef)
  extends SimpleNameLogger {

  def fetchDataTrello(boardIds: Seq[String]): Unit = {
    log.info("Start fetching Trello data...")
    val now = LocalDateTime.now()
    fetchDataActor ! FetchDataTrello(boardIds, now)
  }
}
