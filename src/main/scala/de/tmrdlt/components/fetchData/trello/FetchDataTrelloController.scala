package de.tmrdlt.components.fetchData.trello

import akka.actor.ActorRef
import de.tmrdlt.components.fetchData.trello.FetchDataTrelloActor.FetchDataTrello
import de.tmrdlt.utils.SimpleNameLogger


class FetchDataTrelloController(fetchDataTrelloActor: ActorRef)
  extends SimpleNameLogger {

  def fetchDataTrello(boardIds: Seq[String]): Unit = {
    fetchDataTrelloActor ! FetchDataTrello(boardIds)
  }
}
