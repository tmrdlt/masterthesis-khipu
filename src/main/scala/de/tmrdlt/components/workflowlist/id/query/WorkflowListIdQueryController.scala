package de.tmrdlt.components.workflowlist.id.query

import akka.actor.ActorRef
import de.tmrdlt.constants.WorkflowListColumnType
import de.tmrdlt.constants.WorkflowListColumnType.WorkflowListColumnType
import de.tmrdlt.database.event.{Event, EventDB}
import de.tmrdlt.database.workschedule.{WorkSchedule, WorkScheduleDB}
import de.tmrdlt.models._
import de.tmrdlt.services.{SchedulingService, WorkflowListService}
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger, WorkScheduleUtil}

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.math.Ordered.orderingToOrdered

case class ExecutionOrderWl(apiId: String,
                            title: String,
                            predictedDuration: Long)

class WorkflowListIdQueryController(workflowListService: WorkflowListService,
                                    schedulingService: SchedulingService,
                                    eventDB: EventDB,
                                    workScheduleDB: WorkScheduleDB,
                                    allBestSolutionsActor: ActorRef) extends SimpleNameLogger with OptionExtensions {


  def performTemporalQuery(workflowListApiId: String, userApiId: String): Future[TemporalQueryResultEntity] = {

    val boardFuture = workflowListService.getWorkflowListEntityForId(workflowListApiId)
    val eventsFuture = eventDB.getEvents
    val workScheduleFuture = workScheduleDB.getWorkSchedule

    (for {
      board <- boardFuture.map { wl =>
        if (wl.children.size <= 1) {
          throw new Exception("Board needs at least two columns to perform temporal query")
        } else wl
      }
      events <- eventsFuture
      workSchedule <- workScheduleFuture
      now = workSchedule.schedulingStartDate.getOrElse(LocalDateTime.now())
    } yield {
      // OPEN column is first column
      val openColumn = board.children.head
      val openWorkflowLists = openColumn.children.map(wl => (WorkflowListColumnType.OPEN, wl))
      // IN_PROGRESS columns are all columns between first and last column of board
      val inProgressColumns = board.children.drop(1).dropRight(1)
      val inProgressWorkflowLists = inProgressColumns.flatMap(_.children.map(wl => (WorkflowListColumnType.IN_PROGRESS, wl)))

      // Convert to workflowListTemporal (sum up sublists)
      val allWorkflowListTemporals = (openWorkflowLists ++ inProgressWorkflowLists)
        .flatMap(x => workflowListToTemporal(x._2, x._1))
        .map(wl => getRemainingDuration(now, workSchedule, wl, openColumn.apiId, inProgressColumns.map(_.apiId), events))

      val scheduling = schedulingService.scheduleTasks(now, workSchedule, allWorkflowListTemporals)

      val boardFinishedAt = scheduling.lastOption.map(_.finishedAt).getOrElse(now)

      val result = TemporalQueryResultEntity(
        boardResult = TaskPlanningSolution(
          id = board.id,
          apiId = board.apiId,
          title = board.title,
          startDate = None,
          dueDate = board.getEndDate,
          duration = allWorkflowListTemporals.map(_.remainingDuration).sum,
          startedAt = scheduling.headOption.map(_.startedAt).getOrElse(now),
          finishedAt = boardFinishedAt,
          dueDateKept = !board.getEndDate.exists(_ < boardFinishedAt),
          index = 0
        ),
        tasksResult = scheduling,
        workSchedule = workSchedule
      )
      // ONLY FOR STUDY EVALUATION
      evaluateCurrentOrder(now, workSchedule, allWorkflowListTemporals, result)
      result
    }).flatMap(result => for {
      _ <- eventDB.insertEvent(Event(
        id = 0L,
        apiId = java.util.UUID.randomUUID.toString,
        eventType = EventType.TEMPORAL_QUERY.toString,
        workflowListApiId = workflowListApiId,
        temporalQueryResult = Some(result),
        userApiId = userApiId,
        createdAt = LocalDateTime.now(),
        dataSource = WorkflowListDataSource.Khipu
      ))
    } yield result)
  }

  private def workflowListToTemporal(workflowList: WorkflowListEntity,
                                     columnType: WorkflowListColumnType): Option[WorkflowListTemporal] = {

    if (workflowList.usageType == WorkflowListType.ITEM) {
      workflowList.temporalResource.map { tempEntity =>
        WorkflowListTemporal(
          id = workflowList.id,
          apiId = workflowList.apiId,
          title = workflowList.title,
          workflowListType = workflowList.usageType,
          startDate = tempEntity.startDate,
          dueDate = tempEntity.endDate,
          duration = tempEntity.getDuration,
          remainingDuration = tempEntity.getDuration,
          inColumn = columnType
        )
      }
    } else {
      def getAllTemporalResources(lists: Seq[WorkflowListEntity]): Seq[Option[TemporalResourceEntity]] =
        lists.flatMap(wl => Seq(wl.temporalResource) ++ getAllTemporalResources(wl.children))

      val allTemporalResources = getAllTemporalResources(workflowList.children).flatten
      if (allTemporalResources.nonEmpty) {
        val duration = allTemporalResources.map(_.getDuration).sum
        Some(WorkflowListTemporal(
          id = workflowList.id,
          apiId = workflowList.apiId,
          title = workflowList.title,
          workflowListType = workflowList.usageType,
          startDate = allTemporalResources.flatMap(_.startDate).maxOption, // TODO does this make sense?
          dueDate = allTemporalResources.flatMap(_.endDate).minOption, // TODO does this make sense?
          duration = duration,
          remainingDuration = duration,
          inColumn = columnType
        ))
      } else None
    }

  }

  private def getRemainingDuration(now: LocalDateTime,
                                   workSchedule: WorkSchedule,
                                   workflowList: WorkflowListTemporal,
                                   openApiId: String,
                                   inProgressApiIds: Seq[String],
                                   events: Seq[Event]): WorkflowListTemporal = {

    def getMinutesInProgress(workflowListApiId: String,
                             openApiId: String,
                             inProgressApiIds: Seq[String],
                             events: Seq[Event],
                             now: LocalDateTime): Long = {
      // TODO this doesnt work for nested children as they might not be moved directly into in progress
      val movedToInProgressDateOption = events
        .filter(e => e.workflowListApiId == workflowListApiId &&
          e.oldParentApiId.contains(openApiId) &&
          inProgressApiIds.contains(e.newParentApiId.getOrElse("")))
        // Only use the newest event
        .sortBy(_.createdAt).reverse
        .headOption
        .map(_.createdAt)

      val createdAtInProgressDateOption = events
        .filter(e => e.workflowListApiId == workflowListApiId &&
          e.parentApiId.contains(openApiId) &&
          inProgressApiIds.contains(e.newParentApiId.getOrElse("")))
        // Only use the newest event
        .sortBy(_.createdAt).reverse
        .headOption
        .map(_.createdAt)

      // Time in inProgress: Time passed since moved to IN_PROGRESS OR time passed since created in IN_PROGRESS
      (movedToInProgressDateOption, createdAtInProgressDateOption) match {
        case (Some(fromDate), _) => WorkScheduleUtil.getDurationInMinutesRecursive(workSchedule, fromDate, now)
        case (_, Some(fromDate)) => WorkScheduleUtil.getDurationInMinutesRecursive(workSchedule, fromDate, now)
        case _ => 0
      }
    }

    workflowList.inColumn match {
      case WorkflowListColumnType.OPEN => workflowList
      case WorkflowListColumnType.IN_PROGRESS =>
        // Predicted duration = estimated duration - time in progress
        // When Predicted duration is negative: Task is longer in progress than estimated duration and is considered as
        // done so predicted duration = 0
        workflowList.copy(remainingDuration = Seq(workflowList.remainingDuration - getMinutesInProgress(workflowList.apiId, openApiId, inProgressApiIds, events, now), 0).max)
    }
  }

  private def evaluateCurrentOrder(now: LocalDateTime,
                                   workSchedule: WorkSchedule,
                                   workflowLists: Seq[WorkflowListTemporal],
                                   temporalQueryResult: TemporalQueryResultEntity): Unit = {
    val evaluation = schedulingService.evaluatePlanningOrder(now, workSchedule, workflowLists)
    log.info(s"FinishDate current order: ${evaluation._1}, FinishDate optimal order: ${temporalQueryResult.boardResult.finishedAt}")
    log.info(s"DueDates failed current order: ${evaluation._2}, DueDates failed optimal order: ${temporalQueryResult.tasksResult.count(x => !x.dueDateKept)}")
    log.info(s"Current order: ${workflowLists.map(_.id)}")
  }
}

