package de.tmrdlt.components.workflowlist

import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListDB}
import de.tmrdlt.database.workflowlistresource.{NumericResource, TemporalResource, WorkflowListResourceDB}
import de.tmrdlt.models.{CreateWorkflowListEntity, WorkflowListDataSource, WorkflowListEntity}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class WorkflowListController(workflowListDB: WorkflowListDB,
                             workflowListResourceDB: WorkflowListResourceDB) {

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
      temporalResources <- workflowListResourceDB.getTemporalResources(workflowLists.map(_.id))
      numericResources <- workflowListResourceDB.getNumericResources(workflowLists.map(_.id))
    } yield {
      // Important to return the workflow lists in a ordered way!
      workflowListsToEntities(workflowLists
        // TODO make this an option in the frontend
        // For now: comment out what we want to return
        .filter(_.dataSource == WorkflowListDataSource.Khipu)
        // .filter(_.dataSource == WorkflowListDataSource.Trello)
        // .filter(_.dataSource == WorkflowListDataSource.GitHub)
        , temporalResources, numericResources
      ).sortBy(_.position)
    }
  }

  private def workflowListsToEntities(workflowLists: Seq[WorkflowList],
                                      temporalResources: Seq[TemporalResource],
                                      genericResources: Seq[NumericResource]): Seq[WorkflowListEntity] = {
    workflowLists
      .filter(_.parentId.isEmpty)
      .map { parent =>
        parent.toWorkflowListEntity(
          getChildren(parent.id, workflowLists, 1L, temporalResources, genericResources),
          0L,
          workflowLists,
          temporalResources,
          genericResources
        )
      }
  }

  private def getChildren(parentId: Long,
                          workflowLists: Seq[WorkflowList],
                          level: Long,
                          temporalResources: Seq[TemporalResource],
                          genericResources: Seq[NumericResource]): Seq[WorkflowListEntity] = {
    workflowLists
      .filter(_.parentId.contains(parentId))
      .map { child =>
        child.toWorkflowListEntity(
          getChildren(child.id, workflowLists, level + 1, temporalResources, genericResources),
          level,
          workflowLists,
          temporalResources,
          genericResources
        )
      }
  }
}