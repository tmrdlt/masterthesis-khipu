package de.tmrdlt.constants

object WorkflowListColumnType extends Enumeration {
  type WorkflowListColumnType = Value
  val OPEN, IN_PROGRESS, DONE = Value
}

object AssumedDurationForTasksWithoutDuration {
  val value: Long = 0L
}
