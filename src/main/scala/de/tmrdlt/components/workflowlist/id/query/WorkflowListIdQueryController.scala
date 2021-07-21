package de.tmrdlt.components.workflowlist.id.query

import de.tmrdlt.components.workflowlist.id.query.WorkflowListColumnType.WorkflowListColumnType
import de.tmrdlt.database.event.{Event, EventDB}
import de.tmrdlt.models.WorkflowListType.WorkflowListType
import de.tmrdlt.models.{TemporalQueryResultEntity, TemporalResourceEntity, WorkflowListEntity, WorkflowListType}
import de.tmrdlt.services.WorkflowListService
import de.tmrdlt.utils.SimpleNameLogger

import java.time.temporal.ChronoUnit
import java.time.{DayOfWeek, LocalDateTime}
import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.math.Ordered.orderingToOrdered

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

case class WorkflowListExecutionTreeEvaluation(executionOrder: Seq[String],
                                               calculatedEndDate: LocalDateTime,
                                               numberOfDueDatesFailed: Int) // TODO To show in frontend make object, which contains wl ID and projected due date

object WorkflowListExecutionTreeEvaluation {
  // Note that because `Ordering[A]` is not contravariant, the declaration
  // must be type-parametrized in the event that you want the implicit
  // ordering to apply to subclasses of `Employee`.
  implicit def ordering[A <: WorkflowListExecutionTreeEvaluation]: Ordering[A] =
    Ordering.by(t => (t.numberOfDueDatesFailed, t.calculatedEndDate))
}

class WorkflowListIdQueryController(workflowListService: WorkflowListService,
                                    eventDB: EventDB) extends SimpleNameLogger {

  case class WorkingDate(date: LocalDateTime) {
    def isAtWorkDay: Boolean = workingDaysOfWeek.contains(date.getDayOfWeek)

    def getStartDate: LocalDateTime = date.withHour(startWorkAtHour).withMinute(0)

    def getStopDate: LocalDateTime = date.withHour(stopWorkAtHour).withMinute(0)

    def getNextStartDate: LocalDateTime = date.plusDays(1).withHour(startWorkAtHour).withMinute(0)

    def isBeforeWorkingHours: Boolean = date < getStartDate

    def isAfterWorkingHours: Boolean = date >= getStopDate

    def isInsideWorkingHours: Boolean = date >= getStartDate && date < getStopDate
  }

  def getWorkingDate(localDateTime: LocalDateTime): WorkingDate = WorkingDate(localDateTime.withSecond(0).withNano(0))

  val workingDaysOfWeek: Seq[DayOfWeek] = Seq(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
  val startWorkAtHour: Int = 10
  val stopWorkAtHour: Int = 18
  val assumedDurationForTasksWithoutDuration: Int = 0


  def getDurationOfAllTasks(workflowListApiId: String): Future[TemporalQueryResultEntity] = {
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

        val allWorkflowListsFlattened = workflowListRecFlatten(
          Seq(openColumn),
          WorkflowListColumnType.OPEN,
          openColumn.apiId,
          inProgressColumns.map(_.apiId),
          events,
          now) ++
          workflowListRecFlatten(inProgressColumns,
            WorkflowListColumnType.IN_PROGRESS,
            openColumn.apiId,
            inProgressColumns.map(_.apiId),
            events,
            now)

        val totalDuration = allWorkflowListsFlattened.map(_.predictedDuration).sum

        val totalFinishDateByDuration = getFinishDateRecursive(now, totalDuration)
        val bestExecutionResult = getBestExecutionOrderOfTasks(now, allWorkflowListsFlattened)

        TemporalQueryResultEntity(
          totalDurationMinutes = totalDuration,
          totalFinishDateByDuration = totalFinishDateByDuration,
          openTasksPredictedFinishDate = bestExecutionResult.calculatedEndDate,
          bestExecutionOrder = bestExecutionResult.executionOrder,
          numberOfFailedDueDates = bestExecutionResult.numberOfDueDatesFailed
        )
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
        case (Some(fromDate), _) => ChronoUnit.MINUTES.between(fromDate, now)
        case (_, Some(fromDate)) => ChronoUnit.MINUTES.between(fromDate, now)
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

  @tailrec
  private def getFinishDateRecursive(startDate: LocalDateTime, durationInMinutes: Long): LocalDateTime = {
    val workingDate = getWorkingDate(startDate)
    if (!workingDate.isAtWorkDay) {
      getFinishDateRecursive(workingDate.getNextStartDate, durationInMinutes)
    } else if (workingDate.isAfterWorkingHours) {
      getFinishDateRecursive(workingDate.getNextStartDate, durationInMinutes)
    } else if (workingDate.isBeforeWorkingHours) {
      getFinishDateRecursive(workingDate.getStartDate, durationInMinutes)
    } else {
      val minutesThatCanBeWorkedToday = ChronoUnit.MINUTES.between(workingDate.date, workingDate.getStopDate)
      val actualMinutesWorked = Math.min(durationInMinutes, minutesThatCanBeWorkedToday)
      val minutesRemaining = durationInMinutes - actualMinutesWorked
      //log.info("startWorkAt: " + startWorkAt.toString + " stopWorkAt: " + stopWorkAt.toString + " Minutes that can be worked today: " + minutesThatCanBeWorkedToday.toString + " Actual minutes worked: " + actualMinutesWorked.toString + " Minutes remaining: " + minutesRemaining.toString)
      if (minutesRemaining > 0) {
        getFinishDateRecursive(workingDate.date.plusMinutes(actualMinutesWorked), minutesRemaining)
      } else {
        workingDate.date.plusMinutes(actualMinutesWorked)
      }
      // TODO MAYBE show if possible to finish today: return finish date as LocalDateTime
      //if (startDateRounded.plusMinutes(durationInMinutes).toLocalDate == startDateRounded.toLocalDate) {
      //  startDateRounded.plusMinutes(durationInMinutes)
      //} else {
    }
  }


  private def getBestExecutionOrderOfTasks(now: LocalDateTime, workflowLists: Seq[WorkflowListTemporalQuery]): WorkflowListExecutionTreeEvaluation = {
    workflowLists.filter(_.workflowListType == WorkflowListType.ITEM).permutations.map { tasksPermutation =>
      var startDate = now
      var endDate = now
      var numberOfDueDatesFailed = 0
      // TODO make recursive function
      tasksPermutation
        .foreach { workflowList =>
          startDate = workflowList.temporalResource.flatMap(_.startDate) match {
            case Some(date) => Seq(date, startDate).max
            case _ => endDate
          }
          endDate = getFinishDateRecursive(startDate, workflowList.predictedDuration)
          //log.info("endDate" + endDate)
          if (workflowList.temporalResource.flatMap(_.endDate).exists(_ < endDate)) {
            numberOfDueDatesFailed = numberOfDueDatesFailed + 1
          }
        }
      WorkflowListExecutionTreeEvaluation(tasksPermutation.map(_.title), endDate, numberOfDueDatesFailed)
    }.toSeq.min
  }
}

