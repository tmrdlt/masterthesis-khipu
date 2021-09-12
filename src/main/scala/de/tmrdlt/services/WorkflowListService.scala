package de.tmrdlt.services

import de.tmrdlt.database.user.UserDB
import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListDB}
import de.tmrdlt.database.workflowlistresource._
import de.tmrdlt.models
import de.tmrdlt.models.{WorkflowListDataSource, WorkflowListEntity, WorkflowListsData}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkflowListService(workflowListDB: WorkflowListDB,
                          workflowListResourceDB: WorkflowListResourceDB,
                          userDB: UserDB) {

  // TODO make this an option in the frontend
  val dataSourceFilter: WorkflowListDataSource.Value = models.WorkflowListDataSource.Khipu
  //val dataSourceFilter: WorkflowListDataSource.Value = models.WorkflowListDataSource.GitHub
  //val dataSourceFilter: WorkflowListDataSource.Value = models.WorkflowListDataSource.Trello


  def getWorkflowListEntityForId(workflowListApiId: String): Future[WorkflowListEntity] = {
    val workflowListFuture = workflowListDB.getWorkflowLists.map(_.filter(_.dataSource == dataSourceFilter))

    for {
      workflowList <- workflowListDB.getWorkflowList(workflowListApiId)
      workflowListsData <- getWorkflowListsData(workflowListFuture)
    } yield {
      workflowList.toWorkflowListEntity(getChildren(workflowList.id, 1L, workflowListsData), 0L, workflowListsData)
    }
  }

  def getWorkflowListEntitiesForUser(userApiIdOption: Option[String]): Future[Seq[WorkflowListEntity]] = {
    val workflowListsFuture = (userApiIdOption match {
      case Some(userApiId) => workflowListDB.getWorkflowListsByUserApiId(userApiId)
      case None => workflowListDB.getWorkflowLists
    }).map(_.filter(_.dataSource == dataSourceFilter))

    for {
      workflowListsData <- getWorkflowListsData(workflowListsFuture)
    } yield {
      workflowListsData.workflowLists
        .filter(_.parentId.isEmpty)
        .map(parent => parent.toWorkflowListEntity(getChildren(parent.id, 1L, workflowListsData), 0L, workflowListsData))
        .sortBy(_.position)
    }
  }

  private def getChildren(parentId: Long,
                          level: Long,
                          workflowListsData: WorkflowListsData): Seq[WorkflowListEntity] = {
    workflowListsData.workflowLists
      .filter(_.parentId.contains(parentId))
      .map { child =>
        child.toWorkflowListEntity(
          getChildren(child.id, level + 1, workflowListsData),
          level,
          workflowListsData
        )
      }
  }

  private def getWorkflowListsData(workflowListsFuture: Future[Seq[WorkflowList]]): Future[WorkflowListsData] = {
    for {
      workflowLists <- workflowListsFuture
      temporalResources <- workflowListResourceDB.getTemporalResources(workflowLists.map(_.id))
      numericResources <- workflowListResourceDB.getNumericResources(workflowLists.map(_.id))
      textualResources <- workflowListResourceDB.getTextualResources(workflowLists.map(_.id))
      userResources <- workflowListResourceDB.getUserResources(workflowLists.map(_.id))
      users <- userDB.getUsers
    } yield WorkflowListsData(
      workflowLists = workflowLists,
      temporalResources = temporalResources,
      numericResources = numericResources,
      textualResources = textualResources,
      userResources = userResources,
      users = users
    )
  }
}