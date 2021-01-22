package de.tmrdlt.components.workflowlist

import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListDB}
import de.tmrdlt.models.CreateWorkflowListEntity

import scala.concurrent.Future

class WorkflowListController(workflowListDB: WorkflowListDB) {

  def createWorkflowList(createWorkflowListEntity: CreateWorkflowListEntity): Future[WorkflowList] = {
    workflowListDB.insertWorkflowListQuery(createWorkflowListEntity)
  }
}