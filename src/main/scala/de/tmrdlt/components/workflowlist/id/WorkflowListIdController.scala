package de.tmrdlt.components.workflowlist.id

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.models.UpdateWorkflowListEntity

import scala.concurrent.Future

class WorkflowListIdController(workflowListDB: WorkflowListDB) {

  def updateWorkflowList(workflowListId: Long,
                         updateWorkflowListEntity: UpdateWorkflowListEntity): Future[Int] = {
    updateWorkflowListEntity.newParentId match {
      case Some(parentId) => workflowListDB.assignParentToWorkflowList(workflowListId, parentId)
      case None => Future.successful(0)
    }
    // TODO Add logic to update other fields
  }
}