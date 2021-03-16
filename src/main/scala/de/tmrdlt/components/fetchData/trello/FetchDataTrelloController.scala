package de.tmrdlt.components.fetchData.trello

import akka.actor.ActorRef
import de.tmrdlt.components.fetchData.FetchDataActor.FetchDataTrello
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}


class FetchDataTrelloController(fetchDataActor: ActorRef)
  extends SimpleNameLogger with OptionExtensions {

  def fetchDataTrello(boardIds: Seq[String]): Unit = {
    fetchDataActor ! FetchDataTrello(boardIds)
  }
}
