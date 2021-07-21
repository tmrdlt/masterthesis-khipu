package de.tmrdlt.services

import de.tmrdlt.components.workflowlist.id.query.WorkflowListTemporalQuery
import de.tmrdlt.models.{WorkflowListType, WorkflowListsExecutionResult}

import java.time.temporal.ChronoUnit
import java.time.{DayOfWeek, LocalDateTime}
import scala.annotation.tailrec
import scala.math.Ordered.orderingToOrdered

class WorkScheduleService {

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

  @tailrec
  final def getFinishDateRecursive(startDate: LocalDateTime, durationInMinutes: Long): LocalDateTime = {
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

  @tailrec
  final def getDurationInMinutesRecursive(startDate: LocalDateTime, finishDate: LocalDateTime, durationInMinutes: Long = 0): Long = {
    val startWorkingDate = getWorkingDate(startDate)
    val finishWorkingDate = getWorkingDate(finishDate)

    if (!startWorkingDate.isAtWorkDay) {
      getDurationInMinutesRecursive(startWorkingDate.getNextStartDate, finishDate, durationInMinutes)
    } else if (startWorkingDate.isAfterWorkingHours) {
      getDurationInMinutesRecursive(startWorkingDate.getNextStartDate, finishDate, durationInMinutes)
    } else if (startWorkingDate.isBeforeWorkingHours) {
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
      WorkflowListsExecutionResult(tasksPermutation.map(_.apiId), endDate, numberOfDueDatesFailed)
    }.toSeq.min
  }

}
