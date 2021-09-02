package de.tmrdlt.services.scheduling

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore
import org.optaplanner.core.api.score.stream.{Constraint, ConstraintCollectors, ConstraintFactory, ConstraintProvider}

import java.time.ZoneOffset
import scala.math.Ordered.orderingToOrdered

class TaskScheduleConstraintProvider extends ConstraintProvider {

  override def defineConstraints(constraintFactory: ConstraintFactory): Array[Constraint] =
    Array(
      // Hard constraints
      startDateConflict(constraintFactory),
      // Soft constraints
      dueDateConflict(constraintFactory),
      totalFinishDate(constraintFactory)
    )

  private def startDateConflict(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .from(classOf[TaskWork])
      .filter((taskWork: TaskWork) => taskWork._startedAt < taskWork._task.startDate)
      .penalize("Start date conflict", HardSoftScore.ONE_HARD)

  private def dueDateConflict(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .from(classOf[TaskWork])
      .filter((taskWork: TaskWork) => taskWork._finishedAt > taskWork._task.dueDate)
      .penalize("Due date conflict", HardSoftScore.ONE_SOFT)

  private def totalFinishDate(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .from(classOf[TaskWork])
      .groupBy(ConstraintCollectors.max((taskWork1: TaskWork, taskWork2: TaskWork) => taskWork1._finishedAt.compareTo(taskWork1._finishedAt)))
      .penalizeLong("Total finish date", HardSoftScore.ONE_SOFT, (taskWork: TaskWork) => taskWork._finishedAt.toEpochSecond(ZoneOffset.of("Z")))
}


