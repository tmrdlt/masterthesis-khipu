package de.tmrdlt.services.scheduling

import org.optaplanner.core.api.domain.variable.VariableListener
import org.optaplanner.core.api.score.director.ScoreDirector

import java.time.LocalDateTime

class StartedAtUpdatingVariableListener extends VariableListener[TaskSchedule, Task] {

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
    val previousTask: Task = sourceTask._previousTask
    var finishedAt: LocalDateTime = if (previousTask == null) {
      null
    } else {
      previousTask._finishedAt
    }
    var shadowTask: Task = sourceTask
    var startedAt: LocalDateTime = Seq(shadowTask.startDate, finishedAt).max
    while ( {
      shadowTask != null && !shadowTask._startedAt.isEqual(startedAt)
    }) {
      scoreDirector.beforeVariableChanged(shadowTask, "_startedAt")
      shadowTask._startedAt = startedAt
      shadowTask._finishedAt = shadowTask._startedAt.plusMinutes(shadowTask.duration)
      scoreDirector.afterVariableChanged(shadowTask, "_startedAt")
      finishedAt = shadowTask._finishedAt
      shadowTask = shadowTask._nextTask
      startedAt = Seq(shadowTask.startDate, finishedAt).max
    }
  }
}
