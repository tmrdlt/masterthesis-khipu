package de.tmrdlt.components

import akka.actor.ActorSystem
import de.tmrdlt.components.fetchData.github.{FetchDataGitHubController, FetchDataGitHubRoute}
import de.tmrdlt.components.fetchData.trello.{FetchDataTrelloController, FetchDataTrelloRoute}
import de.tmrdlt.components.health.{HealthController, HealthRoute}
import de.tmrdlt.components.workflowlist.id.convert.{WorkflowListIdConvertController, WorkflowListIdConvertRoute}
import de.tmrdlt.components.workflowlist.id.move.{WorkflowListIdMoveController, WorkflowListIdMoveRoute}
import de.tmrdlt.components.workflowlist.id.reorder.{WorkflowListIdReorderController, WorkflowListIdReorderRoute}
import de.tmrdlt.components.workflowlist.id.{WorkflowListIdController, WorkflowListIdRoute}
import de.tmrdlt.components.workflowlist.{WorkflowListController, WorkflowListRoute}
import de.tmrdlt.connectors.Apis
import de.tmrdlt.database.DBs

class Components(system: ActorSystem) {

  private val dbs = new DBs()
  private val apis = new Apis(system)
  private val actors = new Actors(system, dbs, apis)

  val health = new HealthRoute(new HealthController(actors.healthActor))
  val fetchDataTrello = new FetchDataTrelloRoute(new FetchDataTrelloController(apis.trelloApi, dbs.trelloDB, dbs.workflowListDB, actors.fetchDataTrelloActor))
  val fetchDataGitHub = new FetchDataGitHubRoute(new FetchDataGitHubController(apis.gitHubApi))
  val workflowList = new WorkflowListRoute(new WorkflowListController(dbs.workflowListDB))
  val workflowListId = new WorkflowListIdRoute(new WorkflowListIdController(dbs.workflowListDB))
  val workflowListIdConvert = new WorkflowListIdConvertRoute(new WorkflowListIdConvertController(dbs.workflowListDB))
  val workflowListIdMove = new WorkflowListIdMoveRoute(new WorkflowListIdMoveController(dbs.workflowListDB))
  val workflowListIdReorder = new WorkflowListIdReorderRoute(new WorkflowListIdReorderController(dbs.workflowListDB))
}
