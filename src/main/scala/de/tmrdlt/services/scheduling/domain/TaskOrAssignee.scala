package de.tmrdlt.services.scheduling.domain

import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable

import java.time.LocalDateTime

@PlanningEntity
abstract class TaskOrAssignee {

  @InverseRelationShadowVariable(sourceVariableName = "previousTaskOrEmployee")
  var nextTask: Task = _

  def assignee: Assignee

  def finishedAt: LocalDateTime

}