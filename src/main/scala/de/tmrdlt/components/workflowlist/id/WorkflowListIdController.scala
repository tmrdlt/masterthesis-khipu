package de.tmrdlt.components.workflowlist.id

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.models.UpdateWorkflowListEntity

import java.util.UUID
import scala.concurrent.Future

class WorkflowListIdController(workflowListDB: WorkflowListDB) {

  def updateWorkflowList(workflowListUUID: UUID,
                         updateWorkflowListEntity: UpdateWorkflowListEntity): Future[Int] =
    workflowListDB.updateWorkflowList(workflowListUUID, updateWorkflowListEntity)

  def deleteWorkflowList(workflowListUUID: UUID): Future[Int] =
    workflowListDB.deleteWorkflowList(workflowListUUID)
}