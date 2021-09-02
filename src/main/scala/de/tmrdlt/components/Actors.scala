package de.tmrdlt.components

import akka.actor._
import de.tmrdlt.components.fetchData.FetchDataActor
import de.tmrdlt.components.health.HealthActor
import de.tmrdlt.components.solver.SolverActor
import de.tmrdlt.connectors.Apis
import de.tmrdlt.database.DBs

class Actors(system: ActorSystem,
             dbs: DBs,
             apis: Apis) {

  val healthActor: ActorRef = system.actorOf(
    HealthActor.props(),
    HealthActor.name
  )

  val solverActor: ActorRef = system.actorOf(
    SolverActor.props(),
    SolverActor.name
  )

  val fetchDataActor: ActorRef = system.actorOf(
    FetchDataActor.props(apis.trelloApi, apis.gitHubApi, dbs.workflowListDB, dbs.eventDB),
    FetchDataActor.name
  )
}
