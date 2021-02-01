package de.tmrdlt.database.workflowlist

import de.tmrdlt.database.MyDB._
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.CreateWorkflowListEntity
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}
import slick.sql.SqlAction

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class WorkflowListDB
  extends SimpleNameLogger
    with OptionExtensions {


  def insertWorkflowListQuery(createWorkflowListEntity: CreateWorkflowListEntity): Future[WorkflowList] = {
    createWorkflowListEntity.parentUuid match {
      case Some(uuid) =>
        db.run(
          for {
            parent <- getWorkflowListByUuidQuery(uuid)
            inserted <- (workflowListQuery returning workflowListQuery) += {
              val now = LocalDateTime.now()
              WorkflowList(
                id = 0L,
                uuid = java.util.UUID.randomUUID,
                title = createWorkflowListEntity.title,
                description = createWorkflowListEntity.description,
                parentId = Some(parent.getOrException("no parent for uuid found").id),
                createdAt = now,
                updatedAt = now
              )
            }
          } yield inserted
        )
      case None =>
        db.run((workflowListQuery returning workflowListQuery) += {
          val now = LocalDateTime.now()
          WorkflowList(
            id = 0L,
            uuid = java.util.UUID.randomUUID,
            title = createWorkflowListEntity.title,
            description = createWorkflowListEntity.description,
            parentId = None,
            createdAt = now,
            updatedAt = now
          )
        })
    }
  }

  def assignParentToWorkflowList(workflowListUUID: UUID, parentUuid: UUID): Future[Int] = {
    db.run(
      for {
        workflowListOption <- getWorkflowListByUuidQuery(workflowListUUID)
        parentWorkflowListOption <- getWorkflowListByUuidQuery(parentUuid)
        updated <- (workflowListOption, parentWorkflowListOption) match {
          case (Some(workflowList), Some(parentWorkflowList)) =>
            workflowListQuery
              .filter(_.id === workflowList.id)
              .map(wl => (wl.parentId, wl.updatedAt))
              .update((Some(parentWorkflowList.id), LocalDateTime.now()))
          case (_, _) =>
            DBIO.failed(new Exception(s"Cannot update workflow list, no list for uuid ${workflowListUUID} or parentUuid ${parentUuid} found"))
        }
      } yield updated
    )
  }

  def updateWorkflowList(workflowListUUID: UUID, newTitle: String, newDescription: Option[String]): Future[Int] = {
    db.run(
      for {
        workflowListOption <- getWorkflowListByUuidQuery(workflowListUUID)
        updated <- workflowListOption match {
          case Some(workflowList) =>
            workflowListQuery
              .filter(_.id === workflowList.id)
              .map(wl => (wl.title, wl.description, wl.updatedAt))
              .update((newTitle, newDescription, LocalDateTime.now()))
          case _ =>
            DBIO.failed(new Exception(s"Cannot update workflow list, no list for uuid ${workflowListUUID}"))
        }
      } yield updated
    )
  }

  def deleteWorkflowList(workflowListUUID: UUID): Future[Int] = {
    db.run {
      for {
        workflowListOption <- workflowListQuery.filter(_.uuid === workflowListUUID).result.headOption
        deleted <- workflowListOption match {
          case Some(workflowList) => workflowListQuery.filter(_.id === workflowList.id).delete
          case None => DBIO.failed(new Exception(s"No workflowList for uuid $workflowListUUID found"))
        }
      } yield deleted
    }
  }

  def getWorkflowLists: Future[Seq[WorkflowList]] = {
    db.run(workflowListQuery.result)
  }

  private def getWorkflowListQuery(workflowListId: Long): SqlAction[Option[WorkflowList], NoStream, Effect.Read] =
    workflowListQuery.filter(_.id === workflowListId).result.headOption

  private def getWorkflowListByUuidQuery(workflowListUUID: UUID): SqlAction[Option[WorkflowList], NoStream, Effect.Read] =
    workflowListQuery.filter(_.uuid === workflowListUUID).result.headOption
}
