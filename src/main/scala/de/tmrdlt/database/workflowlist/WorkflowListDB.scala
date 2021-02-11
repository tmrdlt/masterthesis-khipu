package de.tmrdlt.database.workflowlist

import de.tmrdlt.database.MyDB._
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.{ConvertWorkflowListEntity, CreateWorkflowListEntity, MoveWorkflowListEntity, ReorderWorkflowListEntity, UpdateWorkflowListEntity}
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

  def assignParentToWorkflowList(workflowListUuid: UUID, moveWorkflowListEntity: MoveWorkflowListEntity): Future[Int] = {
    moveWorkflowListEntity.newParentUuid match {
      case Some(uuid) =>
        db.run(
          for {
            workflowListOption <- getWorkflowListByUuidQuery(workflowListUuid)
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
                DBIO.failed(new Exception(s"Cannot move workflow list, no list for uuid ${workflowListUuid} or parentUuid ${uuid} found"))
            }
          } yield updated
        )
      case None =>
        db.run(
          for {
            workflowListOption <- getWorkflowListByUuidQuery(workflowListUuid)
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
                DBIO.failed(new Exception(s"Cannot move workflow list, no list for uuid ${workflowListUuid} found"))
            }
          } yield updated
        )
    }
  }

  def updateWorkflowList(workflowListUuid: UUID, updateWorkflowListEntity: UpdateWorkflowListEntity): Future[Int] = {
    db.run(
      for {
        workflowListOption <- getWorkflowListByUuidQuery(workflowListUuid)
        updated <- workflowListOption match {
          case Some(workflowList) =>
            workflowListQuery
              .filter(_.id === workflowList.id)
              .map(wl => (wl.title, wl.description, wl.updatedAt))
              .update((updateWorkflowListEntity.newTitle, updateWorkflowListEntity.newDescription, LocalDateTime.now()))
          case _ =>
            DBIO.failed(new Exception(s"Cannot update workflow list, no list for uuid ${workflowListUuid}"))
        }
      } yield updated
    )
  }

  def convertWorkflowList(workflowListUuid: UUID, convertWorkflowListEntity: ConvertWorkflowListEntity): Future[Int] = {
    db.run(
      for {
        workflowListOption <- getWorkflowListByUuidQuery(workflowListUuid)
        updated <- workflowListOption match {
          case Some(workflowList) =>
            workflowListQuery
              .filter(_.id === workflowList.id)
              .map(wl => (wl.usageType, wl.updatedAt))
              .update((convertWorkflowListEntity.newUsageType, LocalDateTime.now()))
          case _ =>
            DBIO.failed(new Exception(s"Cannot convert workflow list, no list for uuid ${workflowListUuid}"))
        }
      } yield updated
    )
  }

  // https://softwareengineering.stackexchange.com/questions/304593/how-to-store-ordered-information-in-a-relational-database
  def reorderWorkflowList(workflowListUuid: UUID, rwle: ReorderWorkflowListEntity): Future[Int] = {
    // update foo set a=a+123 not possible in slick atm
    // https://github[dot]com/slick/slick/issues/497
    def lowerToHigher(workflowList: WorkflowList) =
      for {
        wflsToUpdate <- workflowListQuery
          .filter(_.parentId === workflowList.parentId)
          .filter(wl => wl.order >= workflowList.order && wl.order <= rwle.newOrderIndex).result
        updated <- DBIO.sequence(wflsToUpdate.map(w => {
          workflowListQuery
            .filter(_.id === w.id)
            .map(_.order)
            .update(w.order - 1)
        }))
      } yield updated

    def higherToLower(workflowList: WorkflowList) =
      for {
        wflsToUpdate <- workflowListQuery
          .filter(_.parentId === workflowList.parentId)
          .filter(wl => wl.order <= workflowList.order && wl.order >= rwle.newOrderIndex).result
        updated <- DBIO.sequence(wflsToUpdate.map(w => {
          workflowListQuery
            .filter(_.id === w.id)
            .map(_.order)
            .update(w.order + 1)
        }))
      } yield updated

    val query =
      for {
        // ToDo check if illegal newOrderIndex (higher as count of collections)
        workflowListOption <- getWorkflowListByUuidQuery(workflowListUuid)
        neighboursUpdated <- workflowListOption match {
          case Some(list) =>
            if (list.order < rwle.newOrderIndex) lowerToHigher(list)
            else if (list.order > rwle.newOrderIndex) higherToLower(list)
            else DBIO.failed(new Exception(s"New Index equals current index. Nothing will be done"))
          case _ =>
            DBIO.failed(new Exception(s"Cannot reorder workflow list, no list for uuid ${workflowListUuid} found"))
        }
        elementUpdated <- workflowListOption match {
          case Some(workflowList) =>
            workflowListQuery
              .filter(_.id === workflowList.id)
              .map(wl => (wl.order, wl.updatedAt))
              .update((rwle.newOrderIndex, LocalDateTime.now()))
          case _ =>
            DBIO.failed(new Exception(s"Cannot reorder workflow list, no list for uuid ${workflowListUuid} found"))
        }
      } yield neighboursUpdated.sum + elementUpdated
    db.run(query.transactionally)
  }

  def deleteWorkflowList(workflowListUuid: UUID): Future[Int] = {
    db.run {
      for {
        workflowListOption <- workflowListQuery.filter(_.uuid === workflowListUuid).result.headOption
        deleted <- workflowListOption match {
          case Some(workflowList) => workflowListQuery.filter(_.id === workflowList.id).delete
          case None => DBIO.failed(new Exception(s"No workflowList for uuid $workflowListUuid found"))
        }
      } yield deleted
    }
  }

  def getWorkflowLists: Future[Seq[WorkflowList]] = {
    db.run(workflowListQuery.result)
  }

  private def getWorkflowListQuery(workflowListId: Long): SqlAction[Option[WorkflowList], NoStream, Effect.Read] =
    workflowListQuery.filter(_.id === workflowListId).result.headOption

  private def getWorkflowListByUuidQuery(workflowListUuid: UUID): SqlAction[Option[WorkflowList], NoStream, Effect.Read] =
    workflowListQuery.filter(_.uuid === workflowListUuid).result.headOption

  private def getHighestOrderIndexByParentId(parentIdOption: Option[Long]): SqlAction[Option[Long], NoStream, Effect.Read] =
    workflowListQuery.filterIf(parentIdOption.isEmpty)(_.parentId.isEmpty)
      .filterOpt(parentIdOption)(_.parentId === _)
      .sortBy(_.order.desc)
      .map(_.order)
      .result
      .headOption
}
