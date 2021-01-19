package de.tmrdlt.components

import akka.actor.ActorSystem
import de.tmrdlt.components.fetchtrelloboard.{fetchTrelloBoardController, fetchTrelloBoardRoute}
import de.tmrdlt.components.health.{HealthController, HealthRoute}
import de.tmrdlt.database.DBs

class Components(system: ActorSystem) {

  private val dbs = new DBs()
  private val actors = new Actors(system, dbs)

  val health = new HealthRoute(new HealthController(actors.healthActor))
  val fetchTrelloBoard = new fetchTrelloBoardRoute(new fetchTrelloBoardController(dbs.workflowListDB))
}
