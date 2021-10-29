package de.tmrdlt.components.workflowlist

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.models.{CreateWorkflowListEntity, WorkflowListEntity}
import de.tmrdlt.services.WorkflowListService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class WorkflowListController(workflowListDB: WorkflowListDB,
                             workflowListService: WorkflowListService) {

  def createWorkflowList(createWorkflowListEntity: CreateWorkflowListEntity, userApiId: String): Future[String] = {
    for {
      workflowList <- workflowListDB.createWorkflowList(createWorkflowListEntity, userApiId)
      _ <- workflowListDB.createWorkflowListBatch(createWorkflowListEntity.children, 0L, Some(workflowList.id), Some(workflowList.apiId), userApiId)
    } yield workflowList.apiId
  }

  def getWorkflowListEntities(userApiIdOption: Option[String]): Future[Seq[WorkflowListEntity]] =
    workflowListService.getWorkflowListEntitiesForUser(userApiIdOption)
}