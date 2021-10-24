package de.tmrdlt.components.workflowlist.id.move

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.models.MoveWorkflowListEntity

import scala.concurrent.Future

class WorkflowListIdMoveController(workflowListDB: WorkflowListDB) {

  def moveWorkflowList(workflowListApiId: String,
                       moveWorkflowListEntity: MoveWorkflowListEntity,
                       userApiId: String): Future[Int] =
    workflowListDB.moveWorkflowList(workflowListApiId, moveWorkflowListEntity, userApiId)
}