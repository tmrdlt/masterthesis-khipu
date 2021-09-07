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
      startAfterStartDate(constraintFactory),
      // Medium constraints
      finishBeforeDueDate(constraintFactory),
      // Soft constraints
      minimizeTotalFinishDate(constraintFactory),
      //doAsManyTasksAsPossible(constraintFactory)
      // TODO maybe add prefer longer tasks constraint...
    )

  private def startAfterStartDate(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .from(classOf[Task])
      .filter((taskWork: Task) => taskWork._startedAt < taskWork.startDate)
      .penalize("A task cannot be started before it's StartDate", HardMediumSoftScore.ONE_HARD)

  private def finishBeforeDueDate(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .from(classOf[Task])
      .filter((taskWork: Task) => taskWork.finishedAt > taskWork.dueDate)
      .penalize("A task should ideally be finished before it's DueDate", HardMediumSoftScore.ONE_MEDIUM)

  private def minimizeTotalFinishDate(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .from(classOf[Task])
      .filter(task => task._nextTask == null)
      .penalize("The final FinishTime should be minimized", HardMediumSoftScore.ONE_SOFT,
        (taskWork: Task) => Math.abs(ChronoUnit.MINUTES.between(LocalDateTime.now(), taskWork.finishedAt)).toInt // TODO check why can't use penalizeLong?
      )

  private def doAsManyTasksAsPossible(constraintFactory: ConstraintFactory): Constraint = {
    constraintFactory
      .from(classOf[Task])
      .filter(task => task.finishedAt > task.dueDate)
      .impact("Try to do as many tasks as possible", HardMediumSoftScore.ofSoft(1000))
  }
}


