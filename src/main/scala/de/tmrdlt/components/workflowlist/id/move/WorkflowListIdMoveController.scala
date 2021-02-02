package de.tmrdlt.components.workflowlist.id.move

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.models.MoveWorkflowListEntity

import java.util.UUID
import scala.concurrent.Future

class WorkflowListIdMoveController(workflowListDB: WorkflowListDB) {

  def moveWorkflowList(workfLowListUuid: UUID, moveWorkflowListEntity: MoveWorkflowListEntity): Future[Int] =
    workflowListDB.assignParentToWorkflowList(workfLowListUuid, moveWorkflowListEntity)
}