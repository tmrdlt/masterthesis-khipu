package de.tmrdlt.utils

import de.tmrdlt.database.workschedule.WorkSchedule

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import scala.annotation.tailrec
import scala.math.Ordered.orderingToOrdered

object WorkScheduleUtil {

  private case class WorkingDate(date: LocalDateTime) {
    def isAtWorkDay(workSchedule: WorkSchedule): Boolean = workSchedule.workingDaysOfWeek.contains(date.getDayOfWeek)

    def getStartDate(workSchedule: WorkSchedule): LocalDateTime = date.withHour(workSchedule.startWorkAtHour).withMinute(0)

    def getStopDate(workSchedule: WorkSchedule): LocalDateTime = date.withHour(workSchedule.stopWorkAtHour).withMinute(0)

    def getNextStartDate(workSchedule: WorkSchedule): LocalDateTime = date.plusDays(1).withHour(workSchedule.startWorkAtHour).withMinute(0)

    def isBeforeStartHour(workSchedule: WorkSchedule): Boolean = date < getStartDate(workSchedule)

    def isAfterOrAtStopHour(workSchedule: WorkSchedule): Boolean = date >= getStopDate(workSchedule)
  }

  private def getWorkingDate(localDateTime: LocalDateTime): WorkingDate = WorkingDate(localDateTime.withSecond(0).withNano(0))

  @tailrec
  def getStartedAtWithinWorkSchedule(workSchedule: WorkSchedule, startDate: LocalDateTime): LocalDateTime = {
    val workingDate = getWorkingDate(startDate)

    if (!workingDate.isAtWorkDay(workSchedule)) {
      getStartedAtWithinWorkSchedule(workSchedule, workingDate.getNextStartDate(workSchedule))
    } else if (workingDate.isAfterOrAtStopHour(workSchedule)) {
      getStartedAtWithinWorkSchedule(workSchedule, workingDate.getNextStartDate(workSchedule))
    } else if (workingDate.isBeforeStartHour(workSchedule)) {
      getStartedAtWithinWorkSchedule(workSchedule, workingDate.getStartDate(workSchedule))
    } else {
      startDate
    }
  }

  @tailrec
  final def getFinishDateRecursive(workSchedule: WorkSchedule, startDate: LocalDateTime, durationInMinutes: Long): LocalDateTime = {
    val workingDate = getWorkingDate(startDate)

    if (!workingDate.isAtWorkDay(workSchedule)) {
      getFinishDateRecursive(workSchedule, workingDate.getNextStartDate(workSchedule), durationInMinutes)
    } else if (workingDate.isAfterOrAtStopHour(workSchedule)) {
      getFinishDateRecursive(workSchedule, workingDate.getNextStartDate(workSchedule), durationInMinutes)
    } else if (workingDate.isBeforeStartHour(workSchedule)) {
      getFinishDateRecursive(workSchedule, workingDate.getStartDate(workSchedule), durationInMinutes)
    } else {
      val minutesThatCanBeWorkedToday = ChronoUnit.MINUTES.between(workingDate.date, workingDate.getStopDate(workSchedule))
      val actualMinutesWorked = Math.min(durationInMinutes, minutesThatCanBeWorkedToday)
      val minutesRemaining = durationInMinutes - actualMinutesWorked
      if (minutesRemaining > 0) {
        getFinishDateRecursive(workSchedule, workingDate.date.plusMinutes(actualMinutesWorked), minutesRemaining)
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
  final def getDurationInMinutesRecursive(workSchedule: WorkSchedule, startDate: LocalDateTime, finishDate: LocalDateTime, durationInMinutes: Long = 0): Long = {
    val startWorkingDate = getWorkingDate(startDate)
    val finishWorkingDate = getWorkingDate(finishDate)

    if (!startWorkingDate.isAtWorkDay(workSchedule)) {
      getDurationInMinutesRecursive(workSchedule, startWorkingDate.getNextStartDate(workSchedule), finishDate, durationInMinutes)
    } else if (startWorkingDate.isAfterOrAtStopHour(workSchedule)) {
      getDurationInMinutesRecursive(workSchedule, startWorkingDate.getNextStartDate(workSchedule), finishDate, durationInMinutes)
    } else if (startWorkingDate.isBeforeStartHour(workSchedule)) {
      getDurationInMinutesRecursive(workSchedule, startWorkingDate.getStartDate(workSchedule), finishDate, durationInMinutes)
    } else {
      if (startWorkingDate.date.isAfter(finishWorkingDate.date)) {
        durationInMinutes
      } else {
        val newDuration = if (finishWorkingDate.date <= startWorkingDate.getStopDate(workSchedule)) {
          durationInMinutes + Math.max(0, ChronoUnit.MINUTES.between(startWorkingDate.date, finishWorkingDate.date))
        } else {
          durationInMinutes + Math.max(0, ChronoUnit.MINUTES.between(startWorkingDate.date, startWorkingDate.getStopDate(workSchedule)))
        }
        getDurationInMinutesRecursive(workSchedule, startWorkingDate.getNextStartDate(workSchedule), finishWorkingDate.date, newDuration)
      }
    }
  }
}
