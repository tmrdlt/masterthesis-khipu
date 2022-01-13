package de.tmrdlt.services.scheduling.domain.solver

import de.tmrdlt.services.scheduling.domain.{Task, TaskOrAssignee, TaskSchedule}
import de.tmrdlt.utils.{SimpleNameLogger, WorkScheduleUtil}
import org.optaplanner.core.api.domain.variable.VariableListener
import org.optaplanner.core.api.score.director.ScoreDirector

import java.time.LocalDateTime
import java.util.Objects

class StartedAtUpdatingVariableListener extends VariableListener[TaskSchedule, Task] with SimpleNameLogger {

  override def beforeEntityAdded(scoreDirector: ScoreDirector[TaskSchedule], task: Task): Unit = {
    // Do nothing
  }

  override def afterEntityAdded(scoreDirector: ScoreDirector[TaskSchedule], task: Task): Unit = {
    updateStartedAt(scoreDirector, task)
  }

  override def beforeVariableChanged(scoreDirector: ScoreDirector[TaskSchedule], task: Task): Unit = {
    // Do nothing
  }

  override def afterVariableChanged(scoreDirector: ScoreDirector[TaskSchedule], task: Task): Unit = {
    updateStartedAt(scoreDirector, task)
  }

  override def beforeEntityRemoved(scoreDirector: ScoreDirector[TaskSchedule], task: Task): Unit = {
    // Do nothing
  }

  override def afterEntityRemoved(scoreDirector: ScoreDirector[TaskSchedule], task: Task): Unit = {
    // Do nothing
  }


  protected def updateStartedAt(scoreDirector: ScoreDirector[TaskSchedule], sourceTask: Task): Unit = {
    val previous: TaskOrAssignee = sourceTask.previousTaskOrEmployee
    var finishedAt: LocalDateTime = if (previous == null) {
      null
    } else {
      previous.finishedAt
    }
    var shadowTask: Task = sourceTask
    var startedAt: LocalDateTime = calculateStartTime(shadowTask, finishedAt)
    while ( {
      shadowTask != null && !Objects.equals(shadowTask.startedAt, startedAt)
    }) {
      scoreDirector.beforeVariableChanged(shadowTask, "startedAt")
      shadowTask.startedAt = startedAt
      scoreDirector.afterVariableChanged(shadowTask, "startedAt")
      finishedAt = shadowTask.finishedAt
      shadowTask = shadowTask.nextTask
      startedAt = calculateStartTime(shadowTask, finishedAt)
    }
  }

  private def calculateStartTime(task: Task, previousFinishedAt: LocalDateTime): LocalDateTime = {
    if (task == null || previousFinishedAt == null) null
    // start at what ever is the latest: startDate of task, finishDate of previous task or the startDate of our project (which is task.now)
    else WorkScheduleUtil.getStartDateWithinWorkSchedule(task.workSchedule, Seq(task.startDate.getOrElse(LocalDateTime.MIN), previousFinishedAt, task.now).max)
  }
}
