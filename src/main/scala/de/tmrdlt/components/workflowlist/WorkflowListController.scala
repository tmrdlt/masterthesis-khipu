package de.tmrdlt.components.workflowlist

import de.tmrdlt.database.temporalcontraint.{TemporalConstraint, TemporalConstraintDB}
import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListDB}
import de.tmrdlt.models.{CreateWorkflowListEntity, WorkflowListDataSource, WorkflowListEntity}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class WorkflowListController(workflowListDB: WorkflowListDB,
                             temporalConstraintDB: TemporalConstraintDB) {

  def createWorkflowList(createWorkflowListEntity: CreateWorkflowListEntity): Future[WorkflowList] = {
    workflowListDB.createWorkflowList(createWorkflowListEntity)
  }

  def getWorkflowListEntities(userApiIdOption: Option[String]): Future[Seq[WorkflowListEntity]] = {
    val workflowListsFuture = userApiIdOption match {
      case Some(userApiId) => workflowListDB.getWorkflowLists(userApiId)
      case None => workflowListDB.getWorkflowLists
    }
    for {
      workflowLists <- workflowListsFuture
      temporalConstraints <- temporalConstraintDB.getTemporalConstraints(workflowLists.map(_.id))
    } yield {
      // Important to return the workflow lists in a ordered way!
      workflowListsToEntities(workflowLists
        // TODO make this an option in the frontend
        // For now: comment out what we want to return
        .filter(_.dataSource == WorkflowListDataSource.Khipu)
        // .filter(_.dataSource == WorkflowListDataSource.Trello)
        // .filter(_.dataSource == WorkflowListDataSource.GitHub)
        , temporalConstraints
      ).sortBy(_.position)
    }
  }

  private def workflowListsToEntities(workflowLists: Seq[WorkflowList],
                                      temporalConstraints: Seq[TemporalConstraint]): Seq[WorkflowListEntity] = {
    workflowLists
      .filter(_.parentId.isEmpty)
      .map { parent =>
        parent.toWorkflowListEntity(
          getChildren(parent.id, workflowLists, 1L, temporalConstraints),
          0L,
          workflowLists,
          temporalConstraints
        )
      }
  }

  private def getChildren(parentId: Long,
                          workflowLists: Seq[WorkflowList],
                          level: Long,
                          temporalConstraints: Seq[TemporalConstraint]): Seq[WorkflowListEntity] = {
    workflowLists
      .filter(_.parentId.contains(parentId))
      .map { child =>
        child.toWorkflowListEntity(
          getChildren(child.id, workflowLists, level + 1, temporalConstraints),
          level,
          workflowLists,
          temporalConstraints
        )
      }
  }
}