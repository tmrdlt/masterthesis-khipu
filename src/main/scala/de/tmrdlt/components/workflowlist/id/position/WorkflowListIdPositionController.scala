package de.tmrdlt.components.workflowlist.id.position

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.models.ReorderWorkflowListEntity

import scala.concurrent.Future

class WorkflowListIdPositionController(workflowListDB: WorkflowListDB) {

  def reorderWorkflowList(workflowListApiId: String, reorderWorkflowListEntity: ReorderWorkflowListEntity,
                          userApiId: String): Future[Int] =
    workflowListDB.reorderWorkflowList(workflowListApiId, reorderWorkflowListEntity, userApiId)

}
