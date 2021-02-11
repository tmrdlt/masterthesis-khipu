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


  def insertWorkflowList(cwle: CreateWorkflowListEntity): Future[WorkflowList] = {
    val query =
      for {
        parentIdOption <- cwle.parentUuid match {
          case Some(uuid) => getWorkflowListByUuidSqlAction(uuid).map {
            case Some(parent) => Some(parent.id)
            case None => throw new Exception(s"Cannot create workflow list. No parent for uuid ${uuid} found.")
          }
          case _ => DBIO.successful(None)
        }
        highestOrderIndexOption <- getHighestOrderIndexByParentIdSqlAction(parentIdOption)
        inserted <- (workflowListQuery returning workflowListQuery) += {
          val now = LocalDateTime.now()
          WorkflowList(
            id = 0L,
            uuid = java.util.UUID.randomUUID,
            title = cwle.title,
            description = cwle.description,
            usageType = cwle.usageType,
            parentId = parentIdOption,
            order = highestOrderIndexOption match {
              case Some(order) => order + 1
              case None => 0
            },
            createdAt = now,
            updatedAt = now
          )
        }
      } yield inserted

    db.run(query)
  }

  def moveWorkflowList(workflowListUuid: UUID, mwle: MoveWorkflowListEntity): Future[Int] = {
    val query =
      for {
        newParentIdOption <- mwle.newParentUuid match {
          case Some(uuid) => getWorkflowListByUuidSqlAction(uuid).map {
            case Some(parent) => Some(parent.id)
            case None => throw new Exception(s"Cannot move workflow list. No parent for uuid ${uuid} found.")
          }
          case _ => DBIO.successful(None)
        }
        workflowListOption <- getWorkflowListByUuidSqlAction(workflowListUuid)
        updated <- workflowListOption match {
          case Some(workflowList) =>
            for {
              neighboursUpdatedOnRemove <- updateNeighboursOnRemove(workflowList)
              neighboursUpdatedOnInsert <- mwle.newOrderIndex match {
                case Some(newOrderIndex) => updateNeighboursOnInsert(newParentIdOption, newOrderIndex)
                case _ => DBIO.successful(Seq(0))
              }
              elementUpdated <- updateElementOnInsert(workflowList, newParentIdOption, mwle.newOrderIndex)
            } yield {
              neighboursUpdatedOnRemove.sum + neighboursUpdatedOnInsert.sum + elementUpdated
            }
          case _ =>
            DBIO.failed(new Exception(s"Cannot move workflow list. No list for uuid ${workflowListUuid} found."))
        }
      } yield updated

    db.run(query.transactionally)
  }

  def updateWorkflowList(workflowListUuid: UUID, uwle: UpdateWorkflowListEntity): Future[Int] = {
    db.run(
      for {
        workflowListOption <- getWorkflowListByUuidSqlAction(workflowListUuid)
        updated <- workflowListOption match {
          case Some(workflowList) =>
            workflowListQuery
              .filter(_.id === workflowList.id)
              .map(wl => (wl.title, wl.description, wl.updatedAt))
              .update((uwle.newTitle, uwle.newDescription, LocalDateTime.now()))
          case _ =>
            DBIO.failed(new Exception(s"Cannot update workflow list. No list for uuid ${workflowListUuid}"))
        }
      } yield updated
    )
  }

  def convertWorkflowList(workflowListUuid: UUID, cwle: ConvertWorkflowListEntity): Future[Int] = {
    db.run(
      for {
        workflowListOption <- getWorkflowListByUuidSqlAction(workflowListUuid)
        updated <- workflowListOption match {
          case Some(workflowList) =>
            workflowListQuery
              .filter(_.id === workflowList.id)
              .map(wl => (wl.usageType, wl.updatedAt))
              .update((cwle.newUsageType, LocalDateTime.now()))
          case _ =>
            DBIO.failed(new Exception(s"Cannot convert workflow list. No list for uuid ${workflowListUuid}"))
        }
      } yield updated
    )
  }

  // https://softwareengineering.stackexchange.com/questions/304593/how-to-store-ordered-information-in-a-relational-database
  def reorderWorkflowList(workflowListUuid: UUID, rwle: ReorderWorkflowListEntity): Future[Int] = {
    val query =
      for {
        // ToDo check if illegal newOrderIndex (higher as count of collections)
        workflowListOption <- getWorkflowListByUuidSqlAction(workflowListUuid)
        updated <- workflowListOption match {
          case Some(workflowList) =>
            for {
              neighboursUpdated <-
                if (workflowList.order < rwle.newOrderIndex) updateNeighboursOnReorderLowToHigh(workflowList, rwle.newOrderIndex)
                else if (workflowList.order > rwle.newOrderIndex) updateNeighboursOnReorderHighToLow(workflowList, rwle.newOrderIndex)
                else DBIO.failed(new Exception(s"Cannot reorder workflow list. New Index equals current index. Nothing will be done"))
              elementUpdated <- workflowListQuery
                .filter(_.id === workflowList.id)
                .map(wl => (wl.order, wl.updatedAt))
                .update((rwle.newOrderIndex, LocalDateTime.now()))
            } yield {
              neighboursUpdated.sum + elementUpdated
            }
          case _ =>
            DBIO.failed(new Exception(s"Cannot reorder workflow list. No list for uuid ${workflowListUuid} found"))
        }
      } yield updated

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

  private def getWorkflowListByUuidSqlAction(workflowListUuid: UUID): SqlAction[Option[WorkflowList], NoStream, Effect.Read] =
    workflowListQuery.filter(_.uuid === workflowListUuid).result.headOption

  private def getWorkflowListByUuidOptionOrExceptionSqlAction(workflowListUuidOption: Option[UUID]): SqlAction[Option[WorkflowList], NoStream, Effect.Read] =
    workflowListQuery
      .filterIf(workflowListUuidOption.isEmpty)(_.uuid === UUID.randomUUID())
      .filterOpt(workflowListUuidOption)(_.uuid === _).result.headOption

  private def getWorkflowListsByParentIdQuery(parentIdOption: Option[Long]): Query[WorkflowListTable, WorkflowListTable#TableElementType, Seq] =
    workflowListQuery
      .filterIf(parentIdOption.isEmpty)(_.parentId.isEmpty)
      .filterOpt(parentIdOption)(_.parentId === _)

  private def getHighestOrderIndexByParentIdSqlAction(parentIdOption: Option[Long]): SqlAction[Option[Long], NoStream, Effect.Read] =
    getWorkflowListsByParentIdQuery(parentIdOption)
      .sortBy(_.order.desc)
      .map(_.order)
      .result
      .headOption

  // Helper functions for order
  // Please notice: update foo set a=a+123 not possible in slick atm, thats why have to get the collection first
  // and the update.
  // https://github[dot]com/slick/slick/issues/497

  private def updateElementOnInsert(workflowList: WorkflowList, newParentId: Option[Long], maybeNewOrderIndex: Option[Long])
  : DBIOAction[Int, NoStream, Effect.Read with Effect.Write] =
    maybeNewOrderIndex match {
      case Some(orderIndex) => workflowListQuery
        .filter(_.id === workflowList.id)
        .map(wl => (wl.parentId, wl.order, wl.updatedAt))
        .update(newParentId, orderIndex, LocalDateTime.now())
      case None => for {
        highestOrderIndexOption <- getHighestOrderIndexByParentIdSqlAction(newParentId)
        elementUpdated <- workflowListQuery
          .filter(_.id === workflowList.id)
          .map(wl => (wl.parentId, wl.order, wl.updatedAt))
          .update(newParentId, highestOrderIndexOption match {
            case Some(order) => order + 1
            case None => 0
          }, LocalDateTime.now())
      } yield elementUpdated
    }

  /**
   * When removing a workflow list from a parent (either because of deletion or move to new parent), we have to
   * update the order of the remaining workflow lists in that parent.
   *
   * @param workflowList WorkflowList that got removed
   * @return Number of updated rows
   */
  private def updateNeighboursOnRemove(workflowList: WorkflowList)
  : DBIOAction[Seq[Int], NoStream, Effect.Read with Effect.Write] =
    for {
      listsToUpdate <- getWorkflowListsByParentIdQuery(workflowList.parentId)
        .filter(wl => wl.order >= workflowList.order).result
      updated <- decrementOrdersUpdate(listsToUpdate)
    } yield updated

  /**
   * When inserting a workflow list into a new parent at a given index (because move to new parent), we have to update
   * the order of the new neighbours.
   *
   * @param newParentId   Parent in which workflow list gets inserted
   * @param newOrderIndex Index at which workflow list gets inserted
   * @return Number of updated rows
   */
  private def updateNeighboursOnInsert(newParentId: Option[Long], newOrderIndex: Long)
  : DBIOAction[Seq[Int], NoStream, Effect.Read with Effect.Write] =
    for {
      listsToUpdate <- getWorkflowListsByParentIdQuery(newParentId)
        .filter(wl => wl.order >= newOrderIndex).result
      updated <- incrementOrdersUpdate(listsToUpdate)
    } yield updated

  private def updateNeighboursOnReorderLowToHigh(workflowList: WorkflowList, newOrderIndex: Long)
  : DBIOAction[Seq[Int], NoStream, Effect.Read with Effect.Write] =
    for {
      listsToUpdate <- getWorkflowListsByParentIdQuery(workflowList.parentId)
        .filter(wl => wl.order >= workflowList.order && wl.order <= newOrderIndex).result
      updated <- decrementOrdersUpdate(listsToUpdate)
    } yield updated

  private def updateNeighboursOnReorderHighToLow(workflowList: WorkflowList, newOrderIndex: Long)
  : DBIOAction[Seq[Int], NoStream, Effect.Read with Effect.Write] =
    for {
      listsToUpdate <- getWorkflowListsByParentIdQuery(workflowList.parentId)
        .filter(wl => wl.order <= workflowList.order && wl.order >= newOrderIndex).result
      updated <- incrementOrdersUpdate(listsToUpdate)
    } yield updated

  private def incrementOrdersUpdate(listsToUpdate: Seq[WorkflowList]): DBIOAction[Seq[Int], NoStream, Effect.Write] =
    DBIO.sequence(listsToUpdate.map(w => {
      workflowListQuery
        .filter(_.id === w.id)
        .map(_.order)
        .update(w.order + 1)
    }))

  private def decrementOrdersUpdate(listsToUpdate: Seq[WorkflowList]): DBIOAction[Seq[Int], NoStream, Effect.Write] =
    DBIO.sequence(listsToUpdate.map(w => {
      workflowListQuery
        .filter(_.id === w.id)
        .map(_.order)
        .update(w.order - 1)
    }))
}
