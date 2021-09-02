package de.tmrdlt.services.scheduling

import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.lookup.PlanningId
import org.optaplanner.core.api.domain.variable._

import java.time.LocalDateTime

@PlanningEntity
case class TaskWork(val id: Long) extends Comparable[TaskWork] {

  @PlanningId
  val internalId: Long = id

  @PlanningVariable(valueRangeProviderRefs = Array("tasksRange"))
  var _task: Task = _

  @PlanningVariable(graphType = PlanningVariableGraphType.CHAINED, valueRangeProviderRefs = Array("tasksWorkRange"))
  var _previousTaskWork: TaskWork = _

  @InverseRelationShadowVariable(sourceVariableName = "_previousTaskWork")
  var _nextTaskWork: TaskWork = _

  @CustomShadowVariable(variableListenerClass = classOf[StartedAtUpdatingVariableListener],
    sources = Array(new PlanningVariableReference(variableName = "_previousTaskWork")))
  var _startedAt: LocalDateTime = _

  var _finishedAt: LocalDateTime = _

  def this() = this(0)

  def compareTo(other: TaskWork): Int = internalId compareTo other.internalId
}
