package de.tmrdlt.utils

import java.time.temporal.ChronoUnit
import java.time.{DayOfWeek, LocalDateTime}
import scala.annotation.tailrec
import scala.math.Ordered.orderingToOrdered

object WorkScheduleUtil {

  private case class WorkingDate(date: LocalDateTime) {
    def isAtWorkDay: Boolean = workingDaysOfWeek.contains(date.getDayOfWeek)

    def getStartDate: LocalDateTime = date.withHour(startWorkAtHour).withMinute(0)

    def getStopDate: LocalDateTime = date.withHour(stopWorkAtHour).withMinute(0)

    def getNextStartDate: LocalDateTime = date.plusDays(1).withHour(startWorkAtHour).withMinute(0)

    def isBeforeStartHour: Boolean = date < getStartDate

    def isAfterOrAtStopHour: Boolean = date >= getStopDate
  }

  private def getWorkingDate(localDateTime: LocalDateTime): WorkingDate = WorkingDate(localDateTime.withSecond(0).withNano(0))

  // TODO maybe move to constants file
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
}
