package de.tmrdlt.components

import akka.actor.ActorSystem
import de.tmrdlt.components.fetchData.github.{FetchDataGitHubController, FetchDataGitHubRoute}
import de.tmrdlt.components.fetchData.trello.{FetchDataTrelloController, FetchDataTrelloRoute}
import de.tmrdlt.components.health.{HealthController, HealthRoute}
import de.tmrdlt.components.user.id.{UserIdController, UserIdRoute}
import de.tmrdlt.components.user.{UserController, UserRoute}
import de.tmrdlt.components.workflowlist.id.convert.{WorkflowListIdConvertController, WorkflowListIdConvertRoute}
import de.tmrdlt.components.workflowlist.id.move.{WorkflowListIdMoveController, WorkflowListIdMoveRoute}
import de.tmrdlt.components.workflowlist.id.query.{WorkflowListIdQueryController, WorkflowListIdQueryRoute}
import de.tmrdlt.components.workflowlist.id.reorder.{WorkflowListIdReorderController, WorkflowListIdReorderRoute}
import de.tmrdlt.components.workflowlist.id.resource.{WorkflowListIdResourceController, WorkflowListIdResourceRoute}
import de.tmrdlt.components.workflowlist.id.{WorkflowListIdController, WorkflowListIdRoute}
import de.tmrdlt.components.workflowlist.{WorkflowListController, WorkflowListRoute}
import de.tmrdlt.connectors.Apis
import de.tmrdlt.database.DBs
import de.tmrdlt.directives.Directives
import de.tmrdlt.services.Services

class Components(system: ActorSystem) {

  private val dbs = new DBs()
  private val apis = new Apis(system)
  private val actors = new Actors(system, dbs, apis)
  private val directives = new Directives(dbs)
  private val services = new Services(dbs)

  val health = new HealthRoute(new HealthController(actors.healthActor))
  val fetchDataGitHub = new FetchDataGitHubRoute(new FetchDataGitHubController(actors.fetchDataActor))
  val fetchDataTrello = new FetchDataTrelloRoute(new FetchDataTrelloController(actors.fetchDataActor))
  val workflowList = new WorkflowListRoute(
    new WorkflowListController(dbs.workflowListDB, services.workflowListService),
    directives.authorizationDirective
  )
  val workflowListId = new WorkflowListIdRoute(
    new WorkflowListIdController(dbs.workflowListDB),
    directives.authorizationDirective
  )
  val workflowListIdConvert = new WorkflowListIdConvertRoute(
    new WorkflowListIdConvertController(dbs.workflowListDB),
    directives.authorizationDirective
  )
  val workflowListIdMove = new WorkflowListIdMoveRoute(
    new WorkflowListIdMoveController(dbs.workflowListDB),
    directives.authorizationDirective
  )
  val workflowListIdQuery = new WorkflowListIdQueryRoute(
    new WorkflowListIdQueryController(services.workflowListService, services.schedulingService, dbs.eventDB, dbs.workScheduleDB),
    directives.authorizationDirective
  )
  val workflowListIdReorder = new WorkflowListIdReorderRoute(
    new WorkflowListIdReorderController(dbs.workflowListDB),
    directives.authorizationDirective
  )
  val workflowListIdResource = new WorkflowListIdResourceRoute(
    new WorkflowListIdResourceController(dbs.workflowListDB, dbs.workflowListResourceDB, dbs.userDB),
    directives.authorizationDirective
  )
  val user = new UserRoute(new UserController(dbs.userDB))
  val userId = new UserIdRoute(new UserIdController(dbs.userDB))
}
