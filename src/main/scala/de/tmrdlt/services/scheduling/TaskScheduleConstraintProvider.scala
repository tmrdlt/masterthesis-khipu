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
      // Medium constraints
      dueDateConflict(constraintFactory),
      // Soft constraints
      totalFinishDate(constraintFactory)
    )

  private def startDateConflict(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .from(classOf[Task])
      .filter((task: Task) => task._startedAt < task.startDate)
      .penalize("Start date conflict", HardSoftScore.ONE_HARD)

  private def dueDateConflict(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .from(classOf[Task])
      .filter((task: Task) => task._finishedAt > task.dueDate)
      .penalize("Due date conflict", HardSoftScore.ONE_SOFT)

  private def totalFinishDate(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .from(classOf[Task])
      .groupBy(ConstraintCollectors.max((task1: Task, task2: Task) => task1._finishedAt.compareTo(task2._finishedAt)))
      .penalizeLong("Total finish date", HardSoftScore.ONE_SOFT, (task: Task) => task._finishedAt.toEpochSecond(ZoneOffset.of("Z")))
}


