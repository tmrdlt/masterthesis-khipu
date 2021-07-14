package de.tmrdlt.components.workflowlist

import de.tmrdlt.database.user.{User, UserDB}
import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListDB}
import de.tmrdlt.database.workflowlistresource._
import de.tmrdlt.models.{CreateWorkflowListEntity, WorkflowListDataSource, WorkflowListEntity}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class WorkflowListController(workflowListDB: WorkflowListDB,
                             workflowListResourceDB: WorkflowListResourceDB,
                             userDB: UserDB) {

  def createWorkflowList(createWorkflowListEntity: CreateWorkflowListEntity): Future[WorkflowList] = {
    workflowListDB.createWorkflowList(createWorkflowListEntity)
  }

  def getWorkflowListEntities(userApiIdOption: Option[String]): Future[Seq[WorkflowListEntity]] = {
    val workflowListsFuture = userApiIdOption match {
      case Some(userApiId) => workflowListDB.getWorkflowLists(userApiId)
      case None => workflowListDB.getWorkflowLists
    }
    val usersFuture = userDB.getUsers
    for {
      workflowLists <- workflowListsFuture
      users <- usersFuture
      temporalResources <- workflowListResourceDB.getTemporalResources(workflowLists.map(_.id))
      numericResources <- workflowListResourceDB.getNumericResources(workflowLists.map(_.id))
      textualResources <- workflowListResourceDB.getTextualResources(workflowLists.map(_.id))
      userResources <- workflowListResourceDB.getUserResources(workflowLists.map(_.id))
    } yield {
      // Important to return the workflow lists in a ordered way!
      workflowListsToEntities(
        workflowLists.filter(_.dataSource == WorkflowListDataSource.Khipu)
        // TODO make this an option in the frontend
        // For now: comment out what we want to return
        // .filter(_.dataSource == WorkflowListDataSource.Trello)
        // .filter(_.dataSource == WorkflowListDataSource.GitHub)
        , temporalResources,
        numericResources,
        textualResources,
        userResources,
        users
      ).sortBy(_.position)
    }
  }

  private def workflowListsToEntities(workflowLists: Seq[WorkflowList],
                                      temporalResources: Seq[TemporalResource],
                                      numericResources: Seq[NumericResource],
                                      textualResources: Seq[TextualResource],
                                      userResources: Seq[UserResource],
                                      users: Seq[User]): Seq[WorkflowListEntity] = {
    workflowLists
      .filter(_.parentId.isEmpty)
      .map { parent =>
        parent.toWorkflowListEntity(
          getChildren(parent.id, workflowLists, 1L, temporalResources, numericResources, textualResources, userResources, users),
          0L,
          workflowLists,
          temporalResources,
          numericResources,
          textualResources,
          userResources,
          users
        )
      }
  }

  private def getChildren(parentId: Long,
                          workflowLists: Seq[WorkflowList],
                          level: Long,
                          temporalResources: Seq[TemporalResource],
                          numericResources: Seq[NumericResource],
                          textualResources: Seq[TextualResource],
                          userResources: Seq[UserResource],
                          users: Seq[User]): Seq[WorkflowListEntity] = {
    workflowLists
      .filter(_.parentId.contains(parentId))
      .map { child =>
        child.toWorkflowListEntity(
          getChildren(child.id, workflowLists, level + 1, temporalResources, numericResources, textualResources, userResources, users),
          level,
          workflowLists,
          temporalResources,
          numericResources,
          textualResources,
          userResources,
          users
        )
      }
  }
}