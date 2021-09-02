package de.tmrdlt.services.scheduling

import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable

import java.time.LocalDateTime

@PlanningEntity
abstract class TaskWorkOrEmployee {

  @InverseRelationShadowVariable(sourceVariableName = "_previousTaskWorkOrEmployee")
  var _nextTaskWork: TaskWork = _

  def employee: Employee

  def finishedAt: LocalDateTime

}