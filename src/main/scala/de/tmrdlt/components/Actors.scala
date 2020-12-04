package de.tmrdlt.components

import akka.actor._
import de.tmrdlt.components.health.HealthActor
import de.tmrdlt.database.DBs

class Actors(system: ActorSystem,
             dbs: DBs) {

  val healthActor: ActorRef = system.actorOf(
    HealthActor.props(),
    HealthActor.name
  )
}
