package de.tmrdlt.database.workflowlist

import de.tmrdlt.database.MyDB._
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.{ConvertWorkflowListEntity, CreateWorkflowListEntity, MoveWorkflowListEntity, UpdateWorkflowListEntity}
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
            highestOrderIndexOption <- getHighestOrderIndexByParentId(Some(parent.getOrException("no parent for uuid found").id))
            inserted <- (workflowListQuery returning workflowListQuery) += {
              val now = LocalDateTime.now()
              WorkflowList(
                id = 0L,
                uuid = java.util.UUID.randomUUID,
                title = createWorkflowListEntity.title,
                description = createWorkflowListEntity.description,
                usageType = createWorkflowListEntity.usageType,
                parentId = Some(parent.getOrException("no parent for uuid found").id),
                order = highestOrderIndexOption match {
                  case Some(order) => order + 1
                  case None => 0
                },
                createdAt = now,
                updatedAt = now
              )
            }
          } yield inserted
        )
      case None =>
        db.run(
          for {
            highestOrderIndexOption <- getHighestOrderIndexByParentId(None)
            inserted <- (workflowListQuery returning workflowListQuery) += {
              val now = LocalDateTime.now()
              WorkflowList(
                id = 0L,
                uuid = java.util.UUID.randomUUID,
                title = createWorkflowListEntity.title,
                description = createWorkflowListEntity.description,
                usageType = createWorkflowListEntity.usageType,
                parentId = None,
                order = highestOrderIndexOption match {
                  case Some(order) => order + 1
                  case None => 0
                },
                createdAt = now,
                updatedAt = now
              )
            }
          } yield inserted
        )
    }
  }

  def assignParentToWorkflowList(workflowListUUID: UUID, moveWorkflowListEntity: MoveWorkflowListEntity): Future[Int] = {
    moveWorkflowListEntity.newParentUuid match {
      case Some(uuid) =>
        db.run(
          for {
            workflowListOption <- getWorkflowListByUuidQuery(workflowListUUID)
            parent <- getWorkflowListByUuidQuery(uuid)
            highestOrderIndexOption <- getHighestOrderIndexByParentId(Some(parent.getOrException("no parent for uuid found").id))
            updated <- (workflowListOption, parent) match {
              case (Some(workflowList), Some(parentWorkflowList)) =>
                workflowListQuery
                  .filter(_.id === workflowList.id)
                  .map(wl => (wl.parentId, wl.order, wl.updatedAt))
                  .update((Some(parentWorkflowList.id), highestOrderIndexOption match {
                    case Some(order) => order + 1
                    case None => 0
                  }, LocalDateTime.now()))
              case (_, _) =>
                DBIO.failed(new Exception(s"Cannot move workflow list, no list for uuid ${workflowListUUID} or parentUuid ${uuid} found"))
            }
          } yield updated
        )
      case None =>
        db.run(
          for {
            workflowListOption <- getWorkflowListByUuidQuery(workflowListUUID)
            highestOrderIndexOption <- getHighestOrderIndexByParentId(None)
            updated <- workflowListOption match {
              case Some(workflowList) =>
                workflowListQuery
                  .filter(_.id === workflowList.id)
                  .map(wl => (wl.parentId, wl.order, wl.updatedAt))
                  .update((None, highestOrderIndexOption match {
                    case Some(order) => order + 1
                    case None => 0
                  }, LocalDateTime.now()))
              case _ =>
                DBIO.failed(new Exception(s"Cannot move workflow list, no list for uuid ${workflowListUUID} found"))
            }
          } yield updated
        )
    }
  }

  def updateWorkflowList(workflowListUUID: UUID, updateWorkflowListEntity: UpdateWorkflowListEntity): Future[Int] = {
    db.run(
      for {
        workflowListOption <- getWorkflowListByUuidQuery(workflowListUUID)
        updated <- workflowListOption match {
          case Some(workflowList) =>
            workflowListQuery
              .filter(_.id === workflowList.id)
              .map(wl => (wl.title, wl.description, wl.updatedAt))
              .update((updateWorkflowListEntity.newTitle, updateWorkflowListEntity.newDescription, LocalDateTime.now()))
          case _ =>
            DBIO.failed(new Exception(s"Cannot update workflow list, no list for uuid ${workflowListUUID}"))
        }
      } yield updated
    )
  }

  def convertWorkflowList(workflowListUUID: UUID, convertWorkflowListEntity: ConvertWorkflowListEntity): Future[Int] = {
    db.run(
      for {
        workflowListOption <- getWorkflowListByUuidQuery(workflowListUUID)
        updated <- workflowListOption match {
          case Some(workflowList) =>
            workflowListQuery
              .filter(_.id === workflowList.id)
              .map(wl => (wl.usageType, wl.updatedAt))
              .update((convertWorkflowListEntity.newUsageType, LocalDateTime.now()))
          case _ =>
            DBIO.failed(new Exception(s"Cannot convert workflow list, no list for uuid ${workflowListUUID}"))
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

  private def getHighestOrderIndexByParentId(parentIdOption: Option[Long]): SqlAction[Option[Long], NoStream, Effect.Read] =
    workflowListQuery.filterIf(parentIdOption.isEmpty)(_.parentId.isEmpty)
      .filterOpt(parentIdOption)(_.parentId === _)
      .sortBy(_.order.desc)
      .map(_.order)
      .result
      .headOption
}
