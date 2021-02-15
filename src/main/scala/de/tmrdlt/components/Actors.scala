package de.tmrdlt.components

import akka.actor._
import de.tmrdlt.components.fetchData.trello.FetchDataTrelloActor
import de.tmrdlt.components.health.HealthActor
import de.tmrdlt.connectors.Apis
import de.tmrdlt.database.DBs

class Actors(system: ActorSystem,
             dbs: DBs,
             apis: Apis) {

  val healthActor: ActorRef = system.actorOf(
    HealthActor.props(),
    HealthActor.name
  )

  val fetchDataTrelloActor: ActorRef = system.actorOf(
    FetchDataTrelloActor.props(apis.trelloApi, dbs.trelloDB),
    FetchDataTrelloActor.name
  )
}
