package de.tmrdlt.components.workflowlist.id.query

import de.tmrdlt.constants.WorkflowListColumnType.WorkflowListColumnType
import de.tmrdlt.constants.{AssumedDurationForTasksWithoutDuration, WorkflowListColumnType}
import de.tmrdlt.database.event.{Event, EventDB}
import de.tmrdlt.models.{TemporalQueryResultEntity, WorkflowListEntity, WorkflowListTemporal}
import de.tmrdlt.services.{SchedulingService, WorkflowListService}
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger, WorkScheduleUtil}

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ExecutionOrderWl(apiId: String,
                            title: String,
                            predictedDuration: Long)

class WorkflowListIdQueryController(workflowListService: WorkflowListService,
                                    schedulingService: SchedulingService,
                                    eventDB: EventDB) extends SimpleNameLogger with OptionExtensions {


  def performTemporalQuery(workflowListApiId: String): Future[TemporalQueryResultEntity] = {
    val now = LocalDateTime.now()

    for {
      board <- workflowListService.getWorkflowListEntityForId(workflowListApiId).map { wl =>
        if (wl.children.size < 2) {
          throw new Exception("Board doesnt have required columns to perform temporal query")
        } else wl
      }
      events <- eventDB.getEvents
    } yield {
      // OPEN column is first column
      val openColumn = board.children.head
      // IN_PROGRESS columns are all columns between first and last column of board
      val inProgressColumns = board.children.drop(1).dropRight(1)

      val allWorkflowListTemporals = recursiveGetAllWorkflowListsWithTemporalResource(Seq(openColumn), WorkflowListColumnType.OPEN) ++
        recursiveGetAllWorkflowListsWithTemporalResource(inProgressColumns, WorkflowListColumnType.IN_PROGRESS)
          .map(wl => getRemainingDuration(now, wl, openColumn.apiId, inProgressColumns.map(_.apiId), events))

      TemporalQueryResultEntity(
        totalDurationMinutes = allWorkflowListTemporals.map(_.remainingDuration).sum,
        bestExecutionResult = schedulingService.scheduleTasks(now, allWorkflowListTemporals)
      )
    }
  }

  private def recursiveGetAllWorkflowListsWithTemporalResource(workflowLists: Seq[WorkflowListEntity],
                                                               columnType: WorkflowListColumnType): Seq[WorkflowListTemporal] = {
    workflowLists.filter(_.temporalResource.isDefined).map { wl =>
      val tempEntity = wl.temporalResource.getOrException("Something impossible happened")
      val duration = tempEntity.durationInMinutes.getOrElse(AssumedDurationForTasksWithoutDuration.value)
      WorkflowListTemporal(
        id = wl.id,
        apiId = wl.apiId,
        title = wl.title,
        workflowListType = wl.usageType,
        startDate = tempEntity.startDate,
        dueDate = tempEntity.endDate,
        duration = duration,
        remainingDuration = duration,
        inColumn = columnType
      )
    } ++ workflowLists.flatMap(wl => recursiveGetAllWorkflowListsWithTemporalResource(wl.children, columnType))
  }

  private def getRemainingDuration(now: LocalDateTime,
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
        .sortBy(_.date)
        .headOption
        .map(_.date)

      val createdAtInProgressDateOption = events
        .filter(e => e.workflowListApiId == workflowListApiId &&
          e.parentApiId.contains(openApiId) &&
          inProgressApiIds.contains(e.newParentApiId.getOrElse("")))
        .sortBy(_.date)
        .headOption
        .map(_.date)

      // Time in inProgress: Time passed since moved to IN_PROGRESS OR time passed since created in IN_PROGRESS
      (movedToInProgressDateOption, createdAtInProgressDateOption) match {
        case (Some(fromDate), _) => WorkScheduleUtil.getDurationInMinutesRecursive(fromDate, now)
        case (_, Some(fromDate)) => WorkScheduleUtil.getDurationInMinutesRecursive(fromDate, now)
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
}

