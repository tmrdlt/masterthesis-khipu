package de.tmrdlt.components.workflowlist

import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListDB}
import de.tmrdlt.models.{CreateWorkflowListEntity, WorkflowListEntity}
import de.tmrdlt.services.WorkflowListService

import scala.concurrent.Future


class WorkflowListController(workflowListDB: WorkflowListDB,
                             workflowListService: WorkflowListService) {

  def createWorkflowList(createWorkflowListEntity: CreateWorkflowListEntity): Future[WorkflowList] = {
    workflowListDB.createWorkflowList(createWorkflowListEntity)
  }

  def getWorkflowListEntities(userApiIdOption: Option[String]): Future[Seq[WorkflowListEntity]] =
    workflowListService.getWorkflowListEntitiesForUser(userApiIdOption)
}