package de.tmrdlt.services.scheduling

import de.tmrdlt.utils.SimpleNameLogger
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
    val previous: TaskOrEmployee = sourceTask._previousTaskOrEmployee
    var finishedAt: LocalDateTime = if (previous == null) {
      null
    } else {
      previous.finishedAt
    }
    var shadowTask: Task = sourceTask
    var startedAt: LocalDateTime = calculateStartTime(shadowTask, finishedAt)
    while ( {
      shadowTask != null && !Objects.equals(shadowTask._startedAt, startedAt)
    }) {
      scoreDirector.beforeVariableChanged(shadowTask, "_startedAt")
      shadowTask._startedAt = startedAt
      scoreDirector.afterVariableChanged(shadowTask, "_startedAt")
      finishedAt = shadowTask.finishedAt
      shadowTask = shadowTask._nextTask
      startedAt = calculateStartTime(shadowTask, finishedAt)
    }
  }

  private def calculateStartTime(task: Task, previousFinishedAt: LocalDateTime): LocalDateTime = {
    if (task == null || previousFinishedAt == null) null
    else Seq(task.startDate, previousFinishedAt).max
  }
}
