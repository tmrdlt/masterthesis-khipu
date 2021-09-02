package de.tmrdlt.services.scheduling

import org.optaplanner.core.api.domain.variable.VariableListener
import org.optaplanner.core.api.score.director.ScoreDirector

import java.time.LocalDateTime

class StartedAtUpdatingVariableListener extends VariableListener[TaskSchedule, TaskWork] {

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
    val previousTask: TaskWork = sourceTask._previousTaskWork
    var finishedAt: LocalDateTime = if (previousTask == null) {
      null
    } else {
      previousTask._finishedAt
    }
    var shadowTask: TaskWork = sourceTask
    var startedAt: LocalDateTime = Seq(shadowTask._task.startDate, finishedAt).max
    while ( {
      shadowTask != null && !shadowTask._startedAt.isEqual(startedAt)
    }) {
      scoreDirector.beforeVariableChanged(shadowTask, "_startedAt")
      shadowTask._startedAt = startedAt
      shadowTask._finishedAt = shadowTask._startedAt.plusMinutes(shadowTask._task.duration)
      scoreDirector.afterVariableChanged(shadowTask, "_startedAt")
      finishedAt = shadowTask._finishedAt
      shadowTask = shadowTask._nextTaskWork
      startedAt = Seq(shadowTask._task.startDate, finishedAt).max
    }
  }
}
