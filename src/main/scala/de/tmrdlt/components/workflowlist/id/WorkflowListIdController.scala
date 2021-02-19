package de.tmrdlt.components.workflowlist.id

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.models.UpdateWorkflowListEntity

import java.util.UUID
import scala.concurrent.Future

class WorkflowListIdController(workflowListDB: WorkflowListDB) {

  def updateWorkflowList(workflowListApiId: String,
                         updateWorkflowListEntity: UpdateWorkflowListEntity): Future[Int] =
    workflowListDB.updateWorkflowList(workflowListApiId, updateWorkflowListEntity)

  def deleteWorkflowList(workflowListApiId: String): Future[Int] =
    workflowListDB.deleteWorkflowList(workflowListApiId)
}