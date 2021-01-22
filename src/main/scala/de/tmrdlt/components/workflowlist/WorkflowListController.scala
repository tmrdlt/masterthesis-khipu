package de.tmrdlt.components.workflowlist

import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListDB}
import de.tmrdlt.models.{CreateWorkflowListEntity, WorkflowListEntity}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class WorkflowListController(workflowListDB: WorkflowListDB) {

  def createWorkflowList(createWorkflowListEntity: CreateWorkflowListEntity): Future[WorkflowList] = {
    workflowListDB.insertWorkflowListQuery(createWorkflowListEntity)
  }

  def getWorkflowListEntities: Future[Seq[WorkflowListEntity]] = {
    for {
      workflowLists <- workflowListDB.getWorkflowLists
    } yield {
      workflowListsToEntities(workflowLists)
    }
  }

  private def workflowListsToEntities(workflowLists: Seq[WorkflowList]): Seq[WorkflowListEntity] = {
    workflowLists
      .filter(_.parentId.isEmpty)
      .map { parent =>
        parent.toWorkflowListEntity(getChildren(parent.id, workflowLists))
      }
  }

  private def getChildren(parentId: Long, workflowLists: Seq[WorkflowList]): Seq[WorkflowListEntity] = {
    workflowLists
      .filter(_.parentId.contains(parentId))
      .map { child =>
        child.toWorkflowListEntity(getChildren(child.id, workflowLists))
      }
  }
}