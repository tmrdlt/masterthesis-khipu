package de.tmrdlt.components.workflowlist.id.query

import de.tmrdlt.components.workflowlist.id.query.WorkflowListColumnType.WorkflowListColumnType
import de.tmrdlt.database.event.{Event, EventDB}
import de.tmrdlt.models.WorkflowListType.WorkflowListType
import de.tmrdlt.models.{TemporalQueryResultEntity, TemporalResourceEntity, WorkflowListEntity}
import de.tmrdlt.services.{WorkScheduleService, WorkflowListService}
import de.tmrdlt.utils.SimpleNameLogger

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// TODO move elsewhere
object WorkflowListColumnType extends Enumeration {
  type WorkflowListColumnType = Value
  val OPEN, IN_PROGRESS = Value
}

case class WorkflowListTemporalQuery(apiId: String,
                                     title: String,
                                     temporalResource: Option[TemporalResourceEntity],
                                     workflowListType: WorkflowListType,
                                     columnType: WorkflowListColumnType,
                                     predictedDuration: Long)

case class ExecutionOrderWl(apiId: String,
                            title: String,
                            predictedDuration: Long,
                           )

class WorkflowListIdQueryController(workflowListService: WorkflowListService,
                                    workScheduleService: WorkScheduleService,
                                    eventDB: EventDB) extends SimpleNameLogger {


  def getDurationOfAllTasks(workflowListApiId: String): Future[Long] = {
    val now = LocalDateTime.now()
    for {
      board <- workflowListService.getWorkflowListEntityForId(workflowListApiId)
      events <- eventDB.getEvents
    } yield {
      if (board.children.size < 2) {
        throw new Exception("Board doesnt have required columns")
      } else {
        // Defined as first column of board
        val openColumn = board.children.head
        // Defined as last column of board
        val doneColumn = board.children.last.children
        // Defined as columns between first and last column of board
        val inProgressColumns = board.children.drop(1).dropRight(1)

        val allWorkflowListsFlattened =
          workflowListRecFlatten(
            Seq(openColumn),
            WorkflowListColumnType.OPEN,
            openColumn.apiId,
            inProgressColumns.map(_.apiId),
            events,
            now
          ) ++
            workflowListRecFlatten(
              inProgressColumns,
              WorkflowListColumnType.IN_PROGRESS,
              openColumn.apiId,
              inProgressColumns.map(_.apiId),
              events,
              now
            )

        val totalDuration = allWorkflowListsFlattened.map(_.predictedDuration).sum

        val bestExecutionResult = workScheduleService.getBestExecutionOrderOfTasks(now, allWorkflowListsFlattened)

        TemporalQueryResultEntity(
          totalDurationMinutes = totalDuration,
          bestExecutionResult = bestExecutionResult
        )
        1L
      }
    }
  }

  private def workflowListRecFlatten(workflowLists: Seq[WorkflowListEntity],
                                     columnType: WorkflowListColumnType,
                                     openApiId: String,
                                     inProgressApiIds: Seq[String],
                                     events: Seq[Event],
                                     now: LocalDateTime): Seq[WorkflowListTemporalQuery] = {
    workflowLists.map(wl =>
      WorkflowListTemporalQuery(
        wl.apiId,
        wl.title,
        wl.temporalResource,
        wl.usageType, columnType,
        getPredictedDurationOfWorkflowList(workflowList = wl, columnType, openApiId, inProgressApiIds, events, now)
      )
    ) ++
      workflowLists.flatMap(wl =>
        workflowListRecFlatten(
          wl.children,
          columnType,
          openApiId,
          inProgressApiIds,
          events,
          now)
      )
  }

  private def getPredictedDurationOfWorkflowList(workflowList: WorkflowListEntity,
                                                 columnType: WorkflowListColumnType,
                                                 openApiId: String,
                                                 inProgressApiIds: Seq[String],
                                                 events: Seq[Event],
                                                 now: LocalDateTime): Long = {

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

      // Time in inProgress: Time passed since moved to inProgress OR time passed sinced created in inProgress
      (movedToInProgressDateOption, createdAtInProgressDateOption) match {
        case (Some(fromDate), _) => workScheduleService.getDurationInMinutesRecursive(fromDate, now)
        case (_, Some(fromDate)) => workScheduleService.getDurationInMinutesRecursive(fromDate, now)
        case _ => 0
      }
    }

    columnType match {
      // Predicted duration = estimated duration
      case WorkflowListColumnType.OPEN => workflowList.temporalResource.flatMap(_.durationInMinutes).getOrElse(0)
      // Predicted duration = estimated duration - time in progress
      // TODO make dependend on schedule
      case WorkflowListColumnType.IN_PROGRESS =>
        val durationFromNow = workflowList.temporalResource.flatMap(_.durationInMinutes) match {
          case Some(duration) => duration - getMinutesInProgress(workflowList.apiId, openApiId, inProgressApiIds, events, now)
          case None => 0
        }
        // When longer in progress than estimated duration: Task should be finished, predicted duration = 0
        if (durationFromNow > 0) {
          durationFromNow
        } else {
          0
        }
    }
  }
}

