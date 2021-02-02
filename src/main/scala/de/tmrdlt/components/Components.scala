package de.tmrdlt.components

import akka.actor.ActorSystem
import de.tmrdlt.components.fetchtrelloboard.{FetchTrelloBoardController, FetchTrelloBoardRoute}
import de.tmrdlt.components.health.{HealthController, HealthRoute}
import de.tmrdlt.components.workflowlist.id.convert.{WorkflowListIdConvertController, WorkflowListIdConvertRoute}
import de.tmrdlt.components.workflowlist.id.move.{WorkflowListIdMoveController, WorkflowListIdMoveRoute}
import de.tmrdlt.components.workflowlist.id.{WorkflowListIdController, WorkflowListIdRoute}
import de.tmrdlt.components.workflowlist.{WorkflowListController, WorkflowListRoute}
import de.tmrdlt.database.DBs

class Components(system: ActorSystem) {

  private val dbs = new DBs()
  private val actors = new Actors(system, dbs)

  val health = new HealthRoute(new HealthController(actors.healthActor))
  val fetchTrelloBoard = new FetchTrelloBoardRoute(new FetchTrelloBoardController(dbs.workflowListDB))
  val workflowList = new WorkflowListRoute(new WorkflowListController(dbs.workflowListDB))
  val workflowListId = new WorkflowListIdRoute(new WorkflowListIdController(dbs.workflowListDB))
  val workflowListIdConvert = new WorkflowListIdConvertRoute(new WorkflowListIdConvertController(dbs.workflowListDB))
  val workflowListIdMove = new WorkflowListIdMoveRoute(new WorkflowListIdMoveController(dbs.workflowListDB))
}
