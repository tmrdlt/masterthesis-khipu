package de.tmrdlt.components.workflowlist

import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListDB}
import de.tmrdlt.models.{CreateWorkflowListEntity, WorkflowListEntity}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class WorkflowListController(workflowListDB: WorkflowListDB) {

  def createWorkflowList(createWorkflowListEntity: CreateWorkflowListEntity): Future[WorkflowList] = {
    workflowListDB.insertWorkflowList(createWorkflowListEntity)
  }

  def getWorkflowListEntities: Future[Seq[WorkflowListEntity]] = {
    for {
      workflowLists <- workflowListDB.getWorkflowLists
    } yield {
      // Important to return the workflow lists in a ordered way!
      workflowListsToEntities(workflowLists).sortBy(_.order)
    }
  }

  private def workflowListsToEntities(workflowLists: Seq[WorkflowList]): Seq[WorkflowListEntity] = {
    workflowLists
      .filter(_.parentId.isEmpty)
      .map { parent =>
        parent.toWorkflowListEntity(getChildren(parent.id, workflowLists, 1L), 0L)
      }
  }

  private def getChildren(parentId: Long, workflowLists: Seq[WorkflowList], level: Long): Seq[WorkflowListEntity] = {
    workflowLists
      .filter(_.parentId.contains(parentId))
      .map { child =>
        child.toWorkflowListEntity(getChildren(child.id, workflowLists, level+1), level)
      }
  }
}