package de.tmrdlt.services.scheduling

import de.tmrdlt.utils.SimpleNameLogger
import org.optaplanner.core.api.domain.variable.VariableListener
import org.optaplanner.core.api.score.director.ScoreDirector

import java.time.LocalDateTime
import java.util.Objects

class StartedAtUpdatingVariableListener extends VariableListener[TaskSchedule, TaskWork] with SimpleNameLogger {

  override def beforeEntityAdded(scoreDirector: ScoreDirector[TaskSchedule], taskWork: TaskWork): Unit = {
    // Do nothing
  }

  override def afterEntityAdded(scoreDirector: ScoreDirector[TaskSchedule], taskWork: TaskWork): Unit = {
    updateStartedAt(scoreDirector, taskWork)
  }

  override def beforeVariableChanged(scoreDirector: ScoreDirector[TaskSchedule], taskWork: TaskWork): Unit = {
    // Do nothing
  }

  override def afterVariableChanged(scoreDirector: ScoreDirector[TaskSchedule], taskWork: TaskWork): Unit = {
    updateStartedAt(scoreDirector, taskWork)
  }

  override def beforeEntityRemoved(scoreDirector: ScoreDirector[TaskSchedule], taskWork: TaskWork): Unit = {
    // Do nothing
  }

  override def afterEntityRemoved(scoreDirector: ScoreDirector[TaskSchedule], taskWork: TaskWork): Unit = {
    // Do nothing
  }


  protected def updateStartedAt(scoreDirector: ScoreDirector[TaskSchedule], sourceTask: TaskWork): Unit = {
    val previous: TaskWorkOrEmployee = sourceTask._previousTaskWorkOrEmployee
    var finishedAt: LocalDateTime = if (previous == null) {
      null
    } else {
      previous.finishedAt
    }
    var shadowTask: TaskWork = sourceTask
    var startedAt: LocalDateTime = calculateStartTime(shadowTask, finishedAt)
    while ( {
      shadowTask != null && !Objects.equals(shadowTask._startedAt, startedAt)
    }) {
      scoreDirector.beforeVariableChanged(shadowTask, "_startedAt")
      shadowTask._startedAt = startedAt
      scoreDirector.afterVariableChanged(shadowTask, "_startedAt")
      finishedAt = shadowTask.finishedAt
      shadowTask = shadowTask._nextTaskWork
      startedAt = calculateStartTime(shadowTask, finishedAt)
    }
  }

  private def calculateStartTime(taskWork: TaskWork, previousFinishedAt: LocalDateTime): LocalDateTime = {
    if (taskWork == null || previousFinishedAt == null) null
    else Seq(taskWork.task.startDate, previousFinishedAt).max
  }
}
