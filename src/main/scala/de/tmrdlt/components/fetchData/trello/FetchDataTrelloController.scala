package de.tmrdlt.components.fetchData.trello

import akka.actor.ActorRef
import de.tmrdlt.components.fetchData.FetchDataActor.FetchDataTrello
import de.tmrdlt.connectors.TrelloApi
import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}


class FetchDataTrelloController(trelloApi: TrelloApi,
                                workflowListDB: WorkflowListDB,
                                fetchDataActor: ActorRef)
  extends SimpleNameLogger with OptionExtensions {

  def fetchDataTrello(boardIds: Seq[String]): Unit = {
    fetchDataActor ! FetchDataTrello(boardIds)
  }
}
