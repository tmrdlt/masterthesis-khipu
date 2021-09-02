package de.tmrdlt.services.scheduling

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore
import org.optaplanner.core.api.score.stream.{Constraint, ConstraintFactory, ConstraintProvider}

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import scala.math.Ordered.orderingToOrdered

class TaskScheduleConstraintProvider extends ConstraintProvider {

  override def defineConstraints(constraintFactory: ConstraintFactory): Array[Constraint] =
    Array(
      // Hard constraints
      startDateConflict(constraintFactory),
      // Medium constraints
      dueDateConflict(constraintFactory),
      // Soft constraints
      totalFinishDate(constraintFactory)
    )

  private def startDateConflict(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .from(classOf[TaskWork])
      .filter((taskWork: TaskWork) => taskWork._startedAt < taskWork.task.startDate)
      .penalize("Start date conflict", HardMediumSoftScore.ONE_HARD)

  private def dueDateConflict(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .from(classOf[TaskWork])
      .filter((taskWork: TaskWork) => taskWork.finishedAt > taskWork.task.dueDate)
      .penalize("Due date conflict", HardMediumSoftScore.ONE_MEDIUM)

  private def totalFinishDate(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .from(classOf[TaskWork])
      .filter(task => task._nextTaskWork == null)
      .penalize("Total finish date", HardMediumSoftScore.ONE_SOFT, (taskWork: TaskWork) => Math.abs(ChronoUnit.MINUTES.between(LocalDateTime.now(), taskWork.finishedAt)).toInt)
}


