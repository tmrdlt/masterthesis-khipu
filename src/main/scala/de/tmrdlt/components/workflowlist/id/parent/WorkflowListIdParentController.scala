package de.tmrdlt.components.workflowlist.id.parent

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.models.MoveWorkflowListEntity

import scala.concurrent.Future

class WorkflowListIdParentController(workflowListDB: WorkflowListDB) {

  def moveWorkflowList(workflowListApiId: String,
                       moveWorkflowListEntity: MoveWorkflowListEntity,
                       userApiId: String): Future[Int] =
    workflowListDB.moveWorkflowList(workflowListApiId, moveWorkflowListEntity, userApiId)
}