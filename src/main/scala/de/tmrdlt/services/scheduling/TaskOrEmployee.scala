package de.tmrdlt.services.scheduling

import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable

import java.time.LocalDateTime

@PlanningEntity
abstract class TaskOrEmployee {

  @InverseRelationShadowVariable(sourceVariableName = "_previousTaskOrEmployee")
  var _nextTask: Task = _

  def employee: Employee

  def finishedAt: LocalDateTime

}