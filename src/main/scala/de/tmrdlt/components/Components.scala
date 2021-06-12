package de.tmrdlt.components

import akka.actor.ActorSystem
import de.tmrdlt.components.fetchData.github.{FetchDataGitHubController, FetchDataGitHubRoute}
import de.tmrdlt.components.fetchData.trello.{FetchDataTrelloController, FetchDataTrelloRoute}
import de.tmrdlt.components.health.{HealthController, HealthRoute}
import de.tmrdlt.components.user.id.{UserIdController, UserIdRoute}
import de.tmrdlt.components.user.{UserController, UserRoute}
import de.tmrdlt.components.workflowlist.id.convert.{WorkflowListIdConvertController, WorkflowListIdConvertRoute}
import de.tmrdlt.components.workflowlist.id.move.{WorkflowListIdMoveController, WorkflowListIdMoveRoute}
import de.tmrdlt.components.workflowlist.id.reorder.{WorkflowListIdReorderController, WorkflowListIdReorderRoute}
import de.tmrdlt.components.workflowlist.id.resource.generic.{WorkflowListIdGenericResourceController, WorkflowListIdGenericResourceRoute}
import de.tmrdlt.components.workflowlist.id.resource.temporal.{WorkflowListIdTempResourceController, WorkflowListIdTempResourceRoute}
import de.tmrdlt.components.workflowlist.id.{WorkflowListIdController, WorkflowListIdRoute}
import de.tmrdlt.components.workflowlist.{WorkflowListController, WorkflowListRoute}
import de.tmrdlt.connectors.Apis
import de.tmrdlt.database.DBs

class Components(system: ActorSystem) {

  private val dbs = new DBs()
  private val apis = new Apis(system)
  private val actors = new Actors(system, dbs, apis)

  val health = new HealthRoute(new HealthController(actors.healthActor))
  val fetchDataTrello = new FetchDataTrelloRoute(new FetchDataTrelloController(actors.fetchDataActor))
  val fetchDataGitHub = new FetchDataGitHubRoute(new FetchDataGitHubController(actors.fetchDataActor))
  val workflowList = new WorkflowListRoute(new WorkflowListController(dbs.workflowListDB, dbs.workflowListResourceDB))
  val workflowListId = new WorkflowListIdRoute(new WorkflowListIdController(dbs.workflowListDB))
  val workflowListIdConvert = new WorkflowListIdConvertRoute(new WorkflowListIdConvertController(dbs.workflowListDB))
  val workflowListIdMove = new WorkflowListIdMoveRoute(new WorkflowListIdMoveController(dbs.workflowListDB))
  val workflowListIdReorder = new WorkflowListIdReorderRoute(new WorkflowListIdReorderController(dbs.workflowListDB))
  val workflowListIdTempResource = new WorkflowListIdTempResourceRoute(
    new WorkflowListIdTempResourceController(dbs.workflowListDB, dbs.workflowListResourceDB)
  )
  val workflowListIdGenericResource = new WorkflowListIdGenericResourceRoute(
    new WorkflowListIdGenericResourceController(dbs.workflowListDB, dbs.workflowListResourceDB)
  )
  val user = new UserRoute(new UserController(dbs.userDB))
  val userId = new UserIdRoute(new UserIdController(dbs.userDB))

}
