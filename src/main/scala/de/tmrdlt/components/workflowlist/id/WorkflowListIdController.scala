package de.tmrdlt.components.workflowlist.id

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.models.UpdateWorkflowListEntity

import java.util.UUID
import scala.concurrent.Future

class WorkflowListIdController(workflowListDB: WorkflowListDB) {

  def updateWorkflowList(workflowListUUID: UUID,
                         updateWorkflowListEntity: UpdateWorkflowListEntity): Future[Int] = {
    updateWorkflowListEntity.newParentUuid match {
      case Some(parentUuid) => workflowListDB.assignParentToWorkflowList(workflowListUUID, parentUuid)
      case None => Future.successful(0)
    }
    // TODO Add logic to update other fields
  }

  def deleteWorkflowList(workflowListUUID: UUID): Future[Int] = {
    workflowListDB.deleteWorkflowList(workflowListUUID)
  }
}