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
      .from(classOf[Task])
      .filter((taskWork: Task) => taskWork._startedAt < taskWork.startDate)
      .penalize("Start date conflict", HardMediumSoftScore.ONE_HARD)

  private def dueDateConflict(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .from(classOf[Task])
      .filter((taskWork: Task) => taskWork.finishedAt > taskWork.dueDate)
      .penalize("Due date conflict", HardMediumSoftScore.ONE_MEDIUM)

  private def totalFinishDate(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .from(classOf[Task])
      .filter(task => task._nextTask == null)
      .penalize("Total finish date", HardMediumSoftScore.ONE_SOFT, (taskWork: Task) => Math.abs(ChronoUnit.MINUTES.between(LocalDateTime.now(), taskWork.finishedAt)).toInt)
}


