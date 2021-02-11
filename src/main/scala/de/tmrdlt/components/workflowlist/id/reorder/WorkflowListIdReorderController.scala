package de.tmrdlt.components.workflowlist.id.reorder

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.models.ReorderWorkflowListEntity

import java.util.UUID
import scala.concurrent.Future

class WorkflowListIdReorderController(workflowListDB: WorkflowListDB) {

  def reorderWorkflowList(workflowListUuid: UUID, reorderWorkflowListEntity: ReorderWorkflowListEntity): Future[Int] =
    workflowListDB.reorderWorkflowList(workflowListUuid, reorderWorkflowListEntity)

}
