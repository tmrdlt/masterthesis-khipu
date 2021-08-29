package de.tmrdlt.services

import de.tmrdlt.components.workflowlist.id.query.WorkflowListTemporalQuery
import de.tmrdlt.models.{WorkflowListExecution, WorkflowListType, WorkflowListsExecutionResult}
import de.tmrdlt.utils.SimpleNameLogger

import java.time.temporal.ChronoUnit
import java.time.{DayOfWeek, LocalDateTime}
import scala.annotation.tailrec
import scala.math.Ordering.Implicits.infixOrderingOps

class WorkScheduleService extends SimpleNameLogger {

  case class WorkingDate(date: LocalDateTime) {
    def isAtWorkDay: Boolean = workingDaysOfWeek.contains(date.getDayOfWeek)

    def getStartDate: LocalDateTime = date.withHour(startWorkAtHour).withMinute(0)

    def getStopDate: LocalDateTime = date.withHour(stopWorkAtHour).withMinute(0)

    def getNextStartDate: LocalDateTime = date.plusDays(1).withHour(startWorkAtHour).withMinute(0)

    def isBeforeStartHour: Boolean = date < getStartDate

    def isAfterOrAtStopHour: Boolean = date >= getStopDate

    def isInsideWorkingHours: Boolean = date >= getStartDate && date < getStopDate

    // TODO check if needed later
    def getPreviousStopDate: LocalDateTime = date.minusDays(1).withHour(stopWorkAtHour).withMinute(0)

    // TODO check if needed later
    def isBeforeOrAtStartHour: Boolean = date <= getStartDate

    // TODO check if needed later
    def isAfterStopHour: Boolean = date > getStopDate
  }

  def getWorkingDate(localDateTime: LocalDateTime): WorkingDate = WorkingDate(localDateTime.withSecond(0).withNano(0))

  val workingDaysOfWeek: Seq[DayOfWeek] = Seq(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
  val startWorkAtHour: Int = 10
  val stopWorkAtHour: Int = 18
  val assumedDurationForTasksWithoutDuration: Int = 0

  @tailrec
  final def getFinishDateRecursive(startDate: LocalDateTime, durationInMinutes: Long): LocalDateTime = {
    val workingDate = getWorkingDate(startDate)

    if (!workingDate.isAtWorkDay) {
      getFinishDateRecursive(workingDate.getNextStartDate, durationInMinutes)
    } else if (workingDate.isAfterOrAtStopHour) {
      getFinishDateRecursive(workingDate.getNextStartDate, durationInMinutes)
    } else if (workingDate.isBeforeStartHour) {
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

  // TODO check if needed later
  @tailrec
  final def getStartDateRecursive(endDate: LocalDateTime, durationInMinutes: Long): LocalDateTime = {
    val workingDate = getWorkingDate(endDate)

    if (!workingDate.isAtWorkDay) {
      getStartDateRecursive(workingDate.getPreviousStopDate, durationInMinutes)
    } else if (workingDate.isBeforeOrAtStartHour) {
      getStartDateRecursive(workingDate.getPreviousStopDate, durationInMinutes)
    } else if (workingDate.isAfterStopHour) {
      getStartDateRecursive(workingDate.getStopDate, durationInMinutes)
    } else {
      val minutesThatCanBeWorkedToday = ChronoUnit.MINUTES.between(workingDate.getStartDate, workingDate.date)
      val actualMinutesWorked = Math.min(durationInMinutes, minutesThatCanBeWorkedToday)
      val minutesRemaining = durationInMinutes - actualMinutesWorked
      //log.info("startWorkAt: " + startWorkAt.toString + " stopWorkAt: " + stopWorkAt.toString + " Minutes that can be worked today: " + minutesThatCanBeWorkedToday.toString + " Actual minutes worked: " + actualMinutesWorked.toString + " Minutes remaining: " + minutesRemaining.toString)
      if (minutesRemaining > 0) {
        getStartDateRecursive(workingDate.date.minusMinutes(actualMinutesWorked), minutesRemaining)
      } else {
        workingDate.date.minusMinutes(actualMinutesWorked)
      }
      // TODO MAYBE show if possible to finish today: return finish date as LocalDateTime
      //if (startDateRounded.plusMinutes(durationInMinutes).toLocalDate == startDateRounded.toLocalDate) {
      //  startDateRounded.plusMinutes(durationInMinutes)
      //} else {
    }
  }

  @tailrec
  final def getDurationInMinutesRecursive(startDate: LocalDateTime, finishDate: LocalDateTime, durationInMinutes: Long = 0): Long = {
    val startWorkingDate = getWorkingDate(startDate)
    val finishWorkingDate = getWorkingDate(finishDate)

    if (!startWorkingDate.isAtWorkDay) {
      getDurationInMinutesRecursive(startWorkingDate.getNextStartDate, finishDate, durationInMinutes)
    } else if (startWorkingDate.isAfterOrAtStopHour) {
      getDurationInMinutesRecursive(startWorkingDate.getNextStartDate, finishDate, durationInMinutes)
    } else if (startWorkingDate.isBeforeStartHour) {
      getDurationInMinutesRecursive(startWorkingDate.getStartDate, finishDate, durationInMinutes)
    } else {
      if (startWorkingDate.date.isAfter(finishWorkingDate.date)) {
        durationInMinutes
      } else {
        val newDuration = if (finishWorkingDate.date <= startWorkingDate.getStopDate) {
          durationInMinutes + Math.max(0, ChronoUnit.MINUTES.between(startWorkingDate.date, finishWorkingDate.date))
        } else {
          durationInMinutes + Math.max(0, ChronoUnit.MINUTES.between(startWorkingDate.date, startWorkingDate.getStopDate))
        }
        getDurationInMinutesRecursive(startWorkingDate.getNextStartDate, finishWorkingDate.date, newDuration)
      }
    }
  }

  def getBestExecutionOrderOfTasks(now: LocalDateTime, workflowLists: Seq[WorkflowListTemporalQuery]): WorkflowListsExecutionResult = {
    workflowLists.filter(_.workflowListType == WorkflowListType.ITEM).permutations.map { tasksPermutation =>
      var endDate = now
      var numberOfDueDatesFailed = 0
      // TODO make recursive function
      val result = tasksPermutation.map { workflowList =>
        val startDate = workflowList.temporalResource.flatMap(_.startDate) match {
          case Some(date) => Seq(date, endDate).max
          case _ => endDate
        }
        endDate = getFinishDateRecursive(startDate, workflowList.predictedDuration)
        if (workflowList.temporalResource.flatMap(_.endDate).exists(_ < endDate)) {
          numberOfDueDatesFailed = numberOfDueDatesFailed + 1
        }
        //log.info(s"${workflowList.title}, StartDate: $startDate, EndDate: $endDate")
        WorkflowListExecution(
          apiId = workflowList.apiId,
          title = workflowList.title,
          duration = workflowList.predictedDuration,
          endDate = endDate,
          dueDate = workflowList.temporalResource.flatMap(_.endDate),
          dueDateKept = !workflowList.temporalResource.flatMap(_.endDate).exists(_ < endDate)
        )
      }
      WorkflowListsExecutionResult(result, endDate, numberOfDueDatesFailed)
    }.toSeq.min
  }

  //def getBestExecutionOrderOfTasks2(now: LocalDateTime, workflowLists: Seq[WorkflowListTemporalQuery]): WorkflowListsExecutionResult = {
  //  val minDate = LocalDateTime.of(1990, 1, 1, 0, 0)
  //  val maxDate = LocalDateTime.of(2050, 1, 1, 0, 0)
  //  var currentEndDate = now
  //  var numberOfDueDatesFailed = 0
  //  var order: Seq[String] = Seq.empty
  //  val wlsWithTemp = workflowLists.filter(_.temporalResource.isDefined)
  //  val wlsWithDueOrStartDates = wlsWithTemp.filter(wl => wl.temporalResource.flatMap(_.endDate).isDefined || wl.temporalResource.flatMap(_.endDate).isDefined).map(wl => (wl.apiId, wl.temporalResource.get))
  //  val wlsWithoutDueDates = wlsWithTemp.filter(_.temporalResource.flatMap(_.endDate).isEmpty).map(wl => (wl.apiId, wl.temporalResource.get))
  //
  //  wlsWithDueOrStartDates
  //    .sortWith((a, b) => Seq(a._2.startDate.getOrElse(maxDate), a._2.endDate.getOrElse(maxDate)).min < Seq(b._2.startDate.getOrElse(maxDate), b._2.endDate.getOrElse(maxDate)).min)
  //    .foreach{ wl => (wl._2.startDate, wl._2.endDate) match {
  //      case (Some(startDate), Some(endDate)) =>
  //      case (Some(startDate), _) =>
  //      case (_, Some(endDate)) => {
  //        val dateUntilTasksCanBeMovedIn = getStartDateRecursive(endDate, wl._2.durationInMinutes.getOrElse(0))
  //
  //      }
  //      case _ =>
  //
  //    }
  //
  //    }
  //  )

  //}

}
