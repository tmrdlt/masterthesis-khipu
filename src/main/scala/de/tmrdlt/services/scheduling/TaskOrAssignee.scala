package de.tmrdlt.services.scheduling

import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable

import java.time.LocalDateTime

@PlanningEntity
abstract class TaskOrAssignee {

  @InverseRelationShadowVariable(sourceVariableName = "_previousTaskOrEmployee")
  var _nextTask: Task = _

  def assignee: Assignee

  def finishedAt: LocalDateTime

}