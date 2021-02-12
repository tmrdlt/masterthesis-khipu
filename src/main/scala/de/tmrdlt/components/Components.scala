package de.tmrdlt.components

import akka.actor.ActorSystem
import de.tmrdlt.components.fetch.trelloboards.{FetchTrelloBoardsController, FetchTrelloBoardsRoute}
import de.tmrdlt.components.health.{HealthController, HealthRoute}
import de.tmrdlt.components.workflowlist.id.convert.{WorkflowListIdConvertController, WorkflowListIdConvertRoute}
import de.tmrdlt.components.workflowlist.id.move.{WorkflowListIdMoveController, WorkflowListIdMoveRoute}
import de.tmrdlt.components.workflowlist.id.reorder.{WorkflowListIdReorderController, WorkflowListIdReorderRoute}
import de.tmrdlt.components.workflowlist.id.{WorkflowListIdController, WorkflowListIdRoute}
import de.tmrdlt.components.workflowlist.{WorkflowListController, WorkflowListRoute}
import de.tmrdlt.connectors.TrelloApi
import de.tmrdlt.database.DBs

class Components(system: ActorSystem) {

  private val dbs = new DBs()
  private val actors = new Actors(system, dbs)

  private val trelloApi: TrelloApi = new TrelloApi()(system)

  val health = new HealthRoute(new HealthController(actors.healthActor))
  val fetchTrelloBoards = new FetchTrelloBoardsRoute(new FetchTrelloBoardsController(trelloApi))
  val workflowList = new WorkflowListRoute(new WorkflowListController(dbs.workflowListDB))
  val workflowListId = new WorkflowListIdRoute(new WorkflowListIdController(dbs.workflowListDB))
  val workflowListIdConvert = new WorkflowListIdConvertRoute(new WorkflowListIdConvertController(dbs.workflowListDB))
  val workflowListIdMove = new WorkflowListIdMoveRoute(new WorkflowListIdMoveController(dbs.workflowListDB))
  val workflowListIdReorder = new WorkflowListIdReorderRoute(new WorkflowListIdReorderController(dbs.workflowListDB))
}
