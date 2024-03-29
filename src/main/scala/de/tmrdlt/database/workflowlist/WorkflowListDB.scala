package de.tmrdlt.database.workflowlist

import de.tmrdlt.database.MyDB._
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.database.event.Event
import de.tmrdlt.models.WorkflowListType.WorkflowListType
import de.tmrdlt.models._
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}
import slick.dbio.Effect
import slick.sql.{FixedSqlAction, SqlAction}

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class WorkflowListDB
  extends SimpleNameLogger
    with OptionExtensions {


  def getWorkflowLists: Future[Seq[WorkflowList]] =
    db.run(workflowListQuery.result)

  def getWorkflowListsByUserApiId(userApiId: String): Future[Seq[WorkflowList]] =
    db.run(workflowListQuery.filter(_.ownerApiId === userApiId).result)

  def getWorkflowList(workflowListApiId: String): Future[WorkflowList] = {
    db.run(getWorkflowListByApiIdSqlAction(workflowListApiId).map {
      case Some(workflowList) => workflowList
      case _ => throw new Exception(s"No workflowList for apiId $workflowListApiId found")
    })
  }

  def getWorkflowListsByParentId(parentId: Option[Long]): Future[Seq[WorkflowList]] =
    db.run(workflowListQuery.filter(_.parentId === parentId).result)

  def insertWorkflowList(workflowList: WorkflowList): Future[WorkflowList] =
    db.run(workflowListQuery returning workflowListQuery += workflowList)

  def insertWorkflowLists(workflowLists: Seq[WorkflowList]): Future[Seq[WorkflowList]] =
    db.run(workflowListQuery returning workflowListQuery ++= workflowLists)

  def createWorkflowListBatch(cwles: Seq[CreateWorkflowListEntity],
                              startingPosition: Long,
                              parentIdOption: Option[Long],
                              parentApiIdOption: Option[String],
                              userApiId: String): Future[Seq[WorkflowList]] = {
    db.run(createWorkflowListBatchAction(cwles, startingPosition, parentIdOption, parentApiIdOption, userApiId).transactionally)
  }

  def convertItemToHigher(workflowList: WorkflowList, cwles: Seq[CreateWorkflowListEntity], newDescription: String, newListType: WorkflowListType, userApiId: String): Future[Int] = {
    val newDescrOption = if (newDescription == "") None else Some(newDescription)
    val query = for {
      workflowListConverted <- convertWorkflowListAction(workflowList, newListType, userApiId)
      workflowListUpdated <- updateDescriptionAction(workflowList, newDescrOption, userApiId)
      startingPosition <- getHighestPositionByParentIdSqlAction(Some(workflowList.id), userApiId)
      itemsInserted <- createWorkflowListBatchAction(cwles, startingPosition.map(pos => pos + 1).getOrElse(0), Some(workflowList.id), Some(workflowList.apiId), userApiId, insertEvent = false)
    } yield workflowListConverted + workflowListUpdated + itemsInserted.length
    db.run(query.transactionally)
  }

  def convertHigherToItem(workflowList: WorkflowList, newDescription: String, userApiId: String): Future[Int] = {
    val newDescrOption = if (newDescription == "") None else Some(newDescription)
    val query = for {
      workflowListConverted <- convertWorkflowListAction(workflowList, WorkflowListType.ITEM, userApiId)
      workflowListUpdated <- updateDescriptionAction(workflowList, newDescrOption, userApiId)
      workflowListsDeleted <- batchDeleteWorkflowListAction(Some(workflowList.id))
    } yield workflowListConverted + workflowListUpdated + workflowListsDeleted
    db.run(query.transactionally)
  }

  def createWorkflowList(cwle: CreateWorkflowListEntity, userApiId: String): Future[WorkflowList] = {
    val now = LocalDateTime.now()
    val query =
      for {
        parentIdOption <- cwle.parentApiId match {
          case Some(apiId) => getWorkflowListByApiIdSqlAction(apiId).map {
            case Some(parent) => Some(parent.id)
            case None => throw new Exception(s"Cannot create workflow list. No parent for apiId ${apiId} found.")
          }
          case _ => DBIO.successful(None)
        }
        highestPositionOption <- getHighestPositionByParentIdSqlAction(parentIdOption, userApiId)
        workflowList <- (workflowListQuery returning workflowListQuery) += {
          WorkflowList(
            id = 0L,
            apiId = java.util.UUID.randomUUID.toString,
            title = cwle.title,
            description = cwle.description,
            parentId = parentIdOption,
            position = highestPositionOption match {
              case Some(position) => position + 1
              case None => 0
            },
            listType = cwle.listType,
            state = Some(WorkflowListState.OPEN),
            dataSource = WorkflowListDataSource.Khipu,
            useCase = None,
            isTemporalConstraintBoard = cwle.isTemporalConstraintBoard,
            ownerApiId = Some(userApiId),
            createdAt = now,
            updatedAt = now
          )
        }
        _ <- (eventQuery returning eventQuery) += {
          Event(
            id = 0L,
            apiId = java.util.UUID.randomUUID.toString,
            eventType = EventType.CREATE.toString,
            workflowListApiId = workflowList.apiId,
            parentApiId = cwle.parentApiId,
            userApiId = userApiId,
            createdAt = now,
            dataSource = WorkflowListDataSource.Khipu
          )
        }
      } yield workflowList

    db.run(query.transactionally)
  }

  def updateWorkflowList(workflowListApiId: String, uwle: UpdateWorkflowListEntity, userApiId: String): Future[Int] = {
    val query =
      for {
        workflowListOption <- getWorkflowListByApiIdSqlAction(workflowListApiId)
        updated <- workflowListOption match {
          case Some(workflowList) =>
            for {
              updated <- workflowListQuery
                .filter(_.id === workflowList.id)
                .map(wl => (wl.title, wl.description, wl.isTemporalConstraintBoard, wl.updatedAt))
                .update((uwle.newTitle, uwle.newDescription, uwle.isTemporalConstraintBoard, LocalDateTime.now()))
              _ <- (eventQuery returning eventQuery) += {
                Event(
                  id = 0L,
                  apiId = java.util.UUID.randomUUID.toString,
                  eventType = EventType.UPDATE.toString,
                  workflowListApiId = workflowListApiId,
                  userApiId = userApiId,
                  createdAt = LocalDateTime.now(),
                  dataSource = WorkflowListDataSource.Khipu
                )
              }
            } yield updated
          case _ =>
            DBIO.failed(new Exception(s"Cannot update workflow list. No list for apiId ${workflowListApiId} found"))
        }
      } yield updated

    db.run(query.transactionally)
  }

  def deleteWorkflowList(workflowListApiId: String, userApiId: String): Future[Int] = {
    val now = LocalDateTime.now()
    val query =
      for {
        workflowListOption <- workflowListQuery.filter(_.apiId === workflowListApiId).result.headOption
        rowsAffected <- workflowListOption match {
          case Some(workflowList) =>
            for {
              parentOption <- workflowList.parentId match {
                case Some(id) => getWorkflowListByIdSqlAction(id)
                case _ => DBIO.successful(None)
              }
              neighboursUpdatedOnRemove <- updateNeighboursOnRemove(workflowList)
              elementDeleted <- workflowListQuery.filter(_.id === workflowList.id).delete
              _ <- (eventQuery returning eventQuery) += {
                Event(
                  id = 0L,
                  apiId = java.util.UUID.randomUUID.toString,
                  eventType = EventType.DELETE.toString,
                  workflowListApiId = workflowList.apiId,
                  parentApiId = parentOption.map(_.apiId),
                  userApiId = userApiId,
                  createdAt = now,
                  dataSource = WorkflowListDataSource.Khipu
                )
              } // TODO what about the cascade delete? Probably an event for every deleted child would be desired
            } yield {
              neighboursUpdatedOnRemove.sum + elementDeleted
            }
          case None => DBIO.failed(new Exception(s"No workflowList for apiId $workflowListApiId found"))
        }
      } yield rowsAffected

    db.run(query.transactionally)
  }

  def convertWorkflowList(workflowList: WorkflowList, newListType: WorkflowListType, userApiId: String): Future[Int] =
    db.run(convertWorkflowListAction(workflowList, newListType, userApiId).transactionally)

  def convertWorkflowListAction(workflowList: WorkflowList, newListType: WorkflowListType, userApiId: String): DBIOAction[Int, NoStream, Effect.Write] = {
    for {
      updated <- workflowListQuery
        .filter(_.id === workflowList.id)
        .map(wl => (wl.listType, wl.updatedAt))
        .update((newListType, LocalDateTime.now()))
      _ <- (eventQuery returning eventQuery) += {
        Event(
          id = 0L,
          apiId = java.util.UUID.randomUUID.toString,
          eventType = EventType.CONVERT.toString,
          workflowListApiId = workflowList.apiId,
          newType = Some(newListType),
          userApiId = userApiId,
          createdAt = LocalDateTime.now(),
          dataSource = WorkflowListDataSource.Khipu
        )
      }
    } yield updated
  }

  def moveWorkflowList(workflowListApiId: String, mwle: MoveWorkflowListEntity, userApiId: String): Future[Int] = {
    val now = LocalDateTime.now()
    val query =
      for {
        newParentOption <- mwle.newParentApiId match {
          case Some(uuid) => getWorkflowListByApiIdSqlAction(uuid).map {
            case Some(parent) => Some(parent)
            case None => throw new Exception(s"Cannot move workflow list. No parent for uuid ${uuid} found.")
          }
          case _ => DBIO.successful(None)
        }
        workflowListOption <- getWorkflowListByApiIdSqlAction(workflowListApiId)
        updated <- workflowListOption match {
          case Some(workflowList) =>
            for {
              neighboursUpdatedOnRemove <- updateNeighboursOnRemove(workflowList)
              neighboursUpdatedOnInsert <- mwle.newPosition match {
                case Some(newOrderIndex) => updateNeighboursOnInsert(newParentOption.map(_.id), newOrderIndex)
                case _ => DBIO.successful(Seq(0))
              }
              elementUpdated <- updateElementOnInsert(workflowList, newParentOption.map(_.id), mwle.newPosition, userApiId)
              oldParentOption <- workflowList.parentId match {
                case Some(id) => getWorkflowListByIdSqlAction(id)
                case _ => DBIO.successful(None)
              }
              _ <- (eventQuery returning eventQuery) += {
                Event(
                  id = 0L,
                  apiId = java.util.UUID.randomUUID.toString,
                  eventType = EventType.MOVE.toString,
                  workflowListApiId = workflowList.apiId,
                  oldParentApiId = oldParentOption.map(_.apiId),
                  newParentApiId = newParentOption.map(_.apiId),
                  userApiId = userApiId,
                  createdAt = now,
                  dataSource = WorkflowListDataSource.Khipu
                )
              }
            } yield {
              neighboursUpdatedOnRemove.sum + neighboursUpdatedOnInsert.sum + elementUpdated
            }
          case _ =>
            DBIO.failed(new Exception(s"Cannot move workflow list. No list for apiId ${workflowListApiId} found."))
        }
      } yield updated

    db.run(query.transactionally)
  }

  def reorderWorkflowList(workflowListApiId: String, rwle: ReorderWorkflowListEntity, userApiId: String): Future[Int] = {
    val query =
      for {
        // ToDo check if illegal newOrderIndex (higher as count of collections)
        workflowListOption <- getWorkflowListByApiIdSqlAction(workflowListApiId)
        updated <- workflowListOption match {
          case Some(workflowList) =>
            for {
              neighboursUpdated <-
                if (workflowList.position < rwle.newPosition) updateNeighboursOnReorderLowToHigh(workflowList, rwle.newPosition)
                else if (workflowList.position > rwle.newPosition) updateNeighboursOnReorderHighToLow(workflowList, rwle.newPosition)
                else DBIO.failed(new Exception(s"Cannot reorder workflow list. New Index equals current index. Nothing will be done"))
              elementUpdated <- workflowListQuery
                .filter(_.id === workflowList.id)
                .map(wl => (wl.position, wl.updatedAt))
                .update((rwle.newPosition, LocalDateTime.now()))
              _ <- (eventQuery returning eventQuery) += {
                Event(
                  id = 0L,
                  apiId = java.util.UUID.randomUUID.toString,
                  eventType = EventType.REORDER.toString,
                  workflowListApiId = workflowListApiId,
                  oldPosition = Some(workflowList.position),
                  newPosition = Some(rwle.newPosition),
                  userApiId = userApiId,
                  createdAt = LocalDateTime.now(),
                  dataSource = WorkflowListDataSource.Khipu
                )
              }
            } yield {
              neighboursUpdated.sum + elementUpdated
            }
          case _ =>
            DBIO.failed(new Exception(s"Cannot reorder workflow list. No list for apiId ${workflowListApiId} found"))
        }
      } yield updated

    db.run(query.transactionally)
  }

  def updateWorkflowListIsTemporalConstraintBoard(workflowListApiId: String,
                                                  isTemporalConstraintBoard: Boolean,
                                                  userApiId: String): Future[Int] = {
    val query =
      for {
        workflowListOption <- getWorkflowListByApiIdSqlAction(workflowListApiId)
        updated <- workflowListOption match {
          case Some(workflowList) =>
            for {
              updated <- workflowListQuery
                .filter(_.id === workflowList.id)
                .map(wl => (wl.isTemporalConstraintBoard, wl.updatedAt))
                .update((Some(isTemporalConstraintBoard), LocalDateTime.now()))
              _ <- (eventQuery returning eventQuery) += {
                Event(
                  id = 0L,
                  apiId = java.util.UUID.randomUUID.toString,
                  eventType = EventType.UPDATE.toString,
                  workflowListApiId = workflowListApiId,
                  userApiId = userApiId,
                  createdAt = LocalDateTime.now(),
                  dataSource = WorkflowListDataSource.Khipu
                )
              }
            } yield updated
          case _ =>
            DBIO.failed(new Exception(s"Cannot update workflow list. No list for apiId ${workflowListApiId} found"))
        }
      } yield updated

    db.run(query)
  }

  private def batchDeleteWorkflowListAction(parentId: Option[Long]): FixedSqlAction[Int, NoStream, Effect.Write] =
    workflowListQuery.filter(_.parentId === parentId).delete


  private def createWorkflowListBatchAction(cwles: Seq[CreateWorkflowListEntity],
                                            startingPosition: Long,
                                            parentIdOption: Option[Long],
                                            parentApiIdOption: Option[String],
                                            userApiId: String,
                                            insertEvent: Boolean = true): DBIOAction[Seq[WorkflowList], NoStream, Effect.Write] = {
    val now = LocalDateTime.now()
    DBIO.sequence(cwles.zipWithIndex.map {
      case (cwle, index) =>
        for {
          workflowList <- (workflowListQuery returning workflowListQuery) += {
            WorkflowList(
              id = 0L,
              apiId = java.util.UUID.randomUUID.toString,
              title = cwle.title,
              description = cwle.description,
              parentId = parentIdOption,
              position = startingPosition + index,
              listType = cwle.listType,
              state = Some(WorkflowListState.OPEN),
              dataSource = WorkflowListDataSource.Khipu,
              useCase = None,
              isTemporalConstraintBoard = cwle.isTemporalConstraintBoard,
              ownerApiId = Some(userApiId),
              createdAt = now,
              updatedAt = now
            )
          }
          _ <- if (insertEvent) {
            (eventQuery returning eventQuery) += {
              Event(
                id = 0L,
                apiId = java.util.UUID.randomUUID.toString,
                eventType = EventType.CREATE.toString,
                workflowListApiId = workflowList.apiId,
                parentApiId = parentApiIdOption,
                userApiId = userApiId,
                createdAt = now,
                dataSource = WorkflowListDataSource.Khipu
              )
            }
          } else DBIO.successful(0)
        } yield workflowList
    })
  }

  private def updateDescriptionAction(workflowList: WorkflowList, newDescription: Option[String], userApiId: String): DBIOAction[Int, NoStream, Effect.Write] = {
    workflowListQuery
      .filter(_.id === workflowList.id)
      .map(wl => (wl.description, wl.updatedAt))
      .update((newDescription, LocalDateTime.now()))
  }

  private def getWorkflowListByIdSqlAction(workflowListId: Long): SqlAction[Option[WorkflowList], NoStream, Effect.Read] =
    workflowListQuery.filter(_.id === workflowListId).result.headOption

  private def getWorkflowListByApiIdSqlAction(workflowListApiId: String): SqlAction[Option[WorkflowList], NoStream, Effect.Read] =
    workflowListQuery.filter(_.apiId === workflowListApiId).result.headOption

  private def getWorkflowListsByParentIdQuery(parentIdOption: Option[Long]): Query[WorkflowListTable, WorkflowListTable#TableElementType, Seq] =
    workflowListQuery
      .filterIf(parentIdOption.isEmpty)(_.parentId.isEmpty)
      .filterOpt(parentIdOption)(_.parentId === _)

  private def getHighestPositionByParentIdSqlAction(parentIdOption: Option[Long], userApiId: String): SqlAction[Option[Long], NoStream, Effect.Read] =
    getWorkflowListsByParentIdQuery(parentIdOption)
      .filter(_.ownerApiId === userApiId)
      .sortBy(_.position.desc)
      .map(_.position)
      .result
      .headOption

  // Helper functions for position
  // Please notice: update foo set a=a+123 not possible in slick atm, thats why have to get the collection first
  // and the update.
  // https://github[dot]com/slick/slick/issues/497

  private def updateElementOnInsert(workflowList: WorkflowList, newParentId: Option[Long], maybeNewPosition: Option[Long], userApiId: String)
  : DBIOAction[Int, NoStream, Effect.Read with Effect.Write] =
    maybeNewPosition match {
      case Some(position) => workflowListQuery
        .filter(_.id === workflowList.id)
        .map(wl => (wl.parentId, wl.position, wl.updatedAt))
        .update(newParentId, position, LocalDateTime.now())
      case None => for {
        highestPositionOption <- getHighestPositionByParentIdSqlAction(newParentId, userApiId)
        elementUpdated <- workflowListQuery
          .filter(_.id === workflowList.id)
          .map(wl => (wl.parentId, wl.position, wl.updatedAt))
          .update(newParentId, highestPositionOption match {
            case Some(position) => position + 1
            case None => 0
          }, LocalDateTime.now())
      } yield elementUpdated
    }

  /**
   * When removing a workflow list from a parent (either because of deletion or move to new parent), we have to
   * update the position of the remaining workflow lists in that parent.
   *
   * @param workflowList WorkflowList that got removed
   * @return Number of updated rows
   */
  private def updateNeighboursOnRemove(workflowList: WorkflowList)
  : DBIOAction[Seq[Int], NoStream, Effect.Read with Effect.Write] =
    for {
      listsToUpdate <- getWorkflowListsByParentIdQuery(workflowList.parentId)
        .filter(wl => wl.position >= workflowList.position).result
      updated <- decrementPositionsUpdate(listsToUpdate)
    } yield updated

  /**
   * When inserting a workflow list into a new parent at a given index (because move to new parent), we have to update
   * the position of the new neighbours.
   *
   * @param newParentId Parent in which workflow list gets inserted
   * @param newPosition Index at which workflow list gets inserted
   * @return Number of updated rows
   */
  private def updateNeighboursOnInsert(newParentId: Option[Long], newPosition: Long)
  : DBIOAction[Seq[Int], NoStream, Effect.Read with Effect.Write] =
    for {
      listsToUpdate <- getWorkflowListsByParentIdQuery(newParentId)
        .filter(wl => wl.position >= newPosition).result
      updated <- incrementPositionsUpdate(listsToUpdate)
    } yield updated

  private def updateNeighboursOnReorderLowToHigh(workflowList: WorkflowList, newPosition: Long)
  : DBIOAction[Seq[Int], NoStream, Effect.Read with Effect.Write] =
    for {
      listsToUpdate <- getWorkflowListsByParentIdQuery(workflowList.parentId)
        .filter(wl => wl.position >= workflowList.position && wl.position <= newPosition).result
      updated <- decrementPositionsUpdate(listsToUpdate)
    } yield updated

  private def updateNeighboursOnReorderHighToLow(workflowList: WorkflowList, newPosition: Long)
  : DBIOAction[Seq[Int], NoStream, Effect.Read with Effect.Write] =
    for {
      listsToUpdate <- getWorkflowListsByParentIdQuery(workflowList.parentId)
        .filter(wl => wl.position <= workflowList.position && wl.position >= newPosition).result
      updated <- incrementPositionsUpdate(listsToUpdate)
    } yield updated

  private def incrementPositionsUpdate(listsToUpdate: Seq[WorkflowList]): DBIOAction[Seq[Int], NoStream, Effect.Write] =
    DBIO.sequence(listsToUpdate.map(w => {
      workflowListQuery
        .filter(_.id === w.id)
        .map(_.position)
        .update(w.position + 1)
    }))

  private def decrementPositionsUpdate(listsToUpdate: Seq[WorkflowList]): DBIOAction[Seq[Int], NoStream, Effect.Write] =
    DBIO.sequence(listsToUpdate.map(w => {
      workflowListQuery
        .filter(_.id === w.id)
        .map(_.position)
        .update(w.position - 1)
    }))
}
