package de.tmrdlt.components.workflowlist.id.convert

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.models.ConvertWorkflowListEntity

import scala.concurrent.Future

class WorkflowListIdConvertController(workflowListDB: WorkflowListDB) {

  def convertWorkflowList(workflowListApiId: String, convertWorkflowListEntity: ConvertWorkflowListEntity): Future[Int] =
    workflowListDB.convertWorkflowList(workflowListApiId, convertWorkflowListEntity)
}