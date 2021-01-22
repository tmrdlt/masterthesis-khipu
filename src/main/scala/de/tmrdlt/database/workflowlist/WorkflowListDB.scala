package de.tmrdlt.database.workflowlist

import de.tmrdlt.database.MyDB._
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.CreateWorkflowListEntity
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}
import slick.sql.SqlAction

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class WorkflowListDB
  extends SimpleNameLogger
    with OptionExtensions {


  def insertWorkflowListQuery(createWorkflowListEntity: CreateWorkflowListEntity): Future[WorkflowList] = {
    db.run(
      (workflowListQuery returning workflowListQuery) += {
        val now = LocalDateTime.now()
        WorkflowList(
          id = 0L,
          uuid = java.util.UUID.randomUUID,
          title = createWorkflowListEntity.title,
          description = createWorkflowListEntity.description,
          parentId = None, // TODO pass as parameter
          createdAt = now,
          updatedAt = now
        )
      }
    )
  }

  def assignParentToWorkflowList(workflowListId: Long, parentId: Long): Future[Int] = {
    db.run(
      for {
        workflowListOption <- getWorkflowListQuery(workflowListId)
        updated <- workflowListOption match {
          case Some(workflowList) =>
            workflowListQuery
              .filter(_.id === workflowList.id)
              .map(wl => (wl.parentId, wl.updatedAt))
              .update((Some(parentId), LocalDateTime.now()))
          case None =>
            DBIO.failed(new Exception(s"Cannot update a workflow list, no list with id ${workflowListId} found"))
        }
      } yield updated
    )
  }

  private def getWorkflowListQuery(workflowListId: Long): SqlAction[Option[WorkflowList], NoStream, Effect.Read] =
    workflowListQuery.filter(_.id === workflowListId).result.headOption
}
