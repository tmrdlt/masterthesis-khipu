package de.tmrdlt.components.workflowlist.id.convert

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.models.ConvertWorkflowListEntity

import java.util.UUID
import scala.concurrent.Future

class WorkflowListIdConvertController(workflowListDB: WorkflowListDB) {

  def convertWorkflowList(workfLowListUuid: UUID, convertWorkflowListEntity: ConvertWorkflowListEntity): Future[Int] =
    workflowListDB.convertWorkflowList(workfLowListUuid, convertWorkflowListEntity)
}