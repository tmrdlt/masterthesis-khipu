package de.tmrdlt.services.scheduling.score

import de.tmrdlt.constants.WorkflowListColumnType
import de.tmrdlt.services.scheduling.domain.Task
import de.tmrdlt.utils.OptionExtensions
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore
import org.optaplanner.core.api.score.stream.{Constraint, ConstraintFactory, ConstraintProvider}

import java.time.temporal.ChronoUnit
import scala.math.Ordered.orderingToOrdered

class TaskScheduleConstraintProvider extends ConstraintProvider with OptionExtensions {

  override def defineConstraints(constraintFactory: ConstraintFactory): Array[Constraint] =
    Array(
      // Hard constraints
      startAfterStartDate(constraintFactory),
      // Medium constraints
      finishBeforeDueDate(constraintFactory),
      // Soft constraints
      minimizeTotalFinishDate(constraintFactory),
      doInProgressTasksFirst(constraintFactory),
      minimizeExceedingOfDueDates(constraintFactory)
      //doAsManyTasksAsPossible(constraintFactory)
    )

  private def startAfterStartDate(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .forEach(classOf[Task])
      .filter((task: Task) => task.startDate.exists(startDate => task.startedAt < startDate))
      .penalize("A task cannot be started before it's StartDate", HardMediumSoftScore.ONE_HARD)

  private def finishBeforeDueDate(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .forEach(classOf[Task])
      .filter((task: Task) => task.dueDate.exists(dueDate => task.finishedAt > dueDate))
      .penalize("A task should ideally be finished before it's DueDate", HardMediumSoftScore.ONE_MEDIUM)

  private def minimizeTotalFinishDate(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .forEach(classOf[Task])
      .filter(task => task.nextTask == null)
      .penalize("The final FinishTime should be minimized", HardMediumSoftScore.ONE_SOFT,
        (task: Task) => Math.abs(ChronoUnit.MINUTES.between(task.now, task.finishedAt)).toInt * Math.abs(ChronoUnit.MINUTES.between(task.now, task.finishedAt)).toInt
      )

  private def minimizeExceedingOfDueDates(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .forEach(classOf[Task])
      .filter((task: Task) => task.dueDate.exists(dueDate => task.finishedAt > dueDate))
      .penalize("When tasks due date failed, exceeding the due dates should be minimized", HardMediumSoftScore.ONE_SOFT,
        (task: Task) => Math.abs(ChronoUnit.MINUTES.between(task.dueDate.getOrException(), task.finishedAt)).toInt
      )

  private def doInProgressTasksFirst(constraintFactory: ConstraintFactory): Constraint =
    constraintFactory
      .forEach(classOf[Task])
      .filter(task => task.inColumn == WorkflowListColumnType.IN_PROGRESS)
      .penalize("In progress tasks should be done first", HardMediumSoftScore.ONE_SOFT,
        (task: Task) => Math.abs(ChronoUnit.MINUTES.between(task.now, task.finishedAt)).toInt
      )

  private def doAsManyTasksAsPossible(constraintFactory: ConstraintFactory): Constraint = {
    constraintFactory
      .forEach(classOf[Task])
      .filter(task => task.dueDate.exists(dueDate => task.finishedAt < dueDate))
      .impact("Try to do as many tasks as possible", HardMediumSoftScore.ofSoft(1000))
  }
}


