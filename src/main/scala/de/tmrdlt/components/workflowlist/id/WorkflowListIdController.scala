package de.tmrdlt.components.workflowlist.id

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.models.UpdateWorkflowListEntity

import scala.concurrent.Future

class WorkflowListIdController(workflowListDB: WorkflowListDB) {

  def updateWorkflowList(workflowListId: Long,
                         updateWorkflowListEntity: UpdateWorkflowListEntity): Future[Int] = {
    workflowListDB.assignParentToWorkflowList(workflowListId, updateWorkflowListEntity.parentId)
  }
}