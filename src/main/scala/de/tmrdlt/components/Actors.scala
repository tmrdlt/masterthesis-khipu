package de.tmrdlt.components

import akka.actor._
import de.tmrdlt.actors.AllBestSchedulingSolutionsActor
import de.tmrdlt.components.fetchData.FetchDataActor
import de.tmrdlt.components.health.HealthActor
import de.tmrdlt.connectors.Apis
import de.tmrdlt.database.DBs
import de.tmrdlt.services.Services

class Actors(system: ActorSystem,
             dbs: DBs,
             apis: Apis,
             services: Services) {

  val healthActor: ActorRef = system.actorOf(
    HealthActor.props(),
    HealthActor.name
  )

  val fetchDataActor: ActorRef = system.actorOf(
    FetchDataActor.props(apis.trelloApi, apis.gitHubApi, dbs.workflowListDB, dbs.eventDB),
    FetchDataActor.name
  )

  val allBestSolutionsActor: ActorRef = system.actorOf(
    AllBestSchedulingSolutionsActor.props(services.schedulingService),
    AllBestSchedulingSolutionsActor.name
  )
}
