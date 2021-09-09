package de.tmrdlt.constants

import java.time.DayOfWeek

object WorkflowListColumnType extends Enumeration {
  type WorkflowListColumnType = Value
  val OPEN, IN_PROGRESS, DONE = Value
}

object AssumedDurationForTasksWithoutDuration {
  val value: Long = 0L
}

case object WorkSchedule {
  val workingDaysOfWeek: Seq[DayOfWeek] = Seq(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
  val startWorkAtHour: Int = 10
  val stopWorkAtHour: Int = 18
}