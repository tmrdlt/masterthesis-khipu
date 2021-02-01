package de.tmrdlt.components.workflowlist.id

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.models.UpdateWorkflowListEntity

import java.util.UUID
import scala.concurrent.Future

class WorkflowListIdController(workflowListDB: WorkflowListDB) {

  def updateWorkflowList(workflowListUUID: UUID,
                         uwle: UpdateWorkflowListEntity): Future[Int] = {
    (uwle.newTitle, uwle.newParentUuid) match {
      case (None, Some(parentUuid)) => workflowListDB.assignParentToWorkflowList(workflowListUUID, parentUuid)
      case (Some(newTitle), None) => workflowListDB.updateWorkflowList(workflowListUUID, newTitle, uwle.newDescription)
      case _ => Future.successful(0)
    }
  }

  def deleteWorkflowList(workflowListUUID: UUID): Future[Int] = {
    workflowListDB.deleteWorkflowList(workflowListUUID)
  }
}