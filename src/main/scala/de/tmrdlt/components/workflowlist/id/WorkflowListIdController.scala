package de.tmrdlt.components.workflowlist.id

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.models.UpdateWorkflowListEntity

import scala.concurrent.Future

class WorkflowListIdController(workflowListDB: WorkflowListDB) {

  def updateWorkflowList(workflowListApiId: String,
                         updateWorkflowListEntity: UpdateWorkflowListEntity,
                         userApiId: String): Future[Int] =
    workflowListDB.updateWorkflowList(workflowListApiId, updateWorkflowListEntity, userApiId)

  def deleteWorkflowList(workflowListApiId: String, userApiId: String): Future[Int] =
    workflowListDB.deleteWorkflowList(workflowListApiId, userApiId)
}