package de.tmrdlt.services.scheduling

import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.lookup.PlanningId
import org.optaplanner.core.api.domain.variable._

import java.time.LocalDateTime

@PlanningEntity
case class TaskWork(val id: Long,
                    val task: Task) extends TaskWorkOrEmployee with Comparable[TaskWork] {

  @PlanningId
  val internalId: Long = id

  @AnchorShadowVariable(sourceVariableName = "_previousTaskWorkOrEmployee")
  var employee: Employee = _

  @PlanningVariable(graphType = PlanningVariableGraphType.CHAINED, valueRangeProviderRefs = Array("tasksWorkRange", "employeeRange"))
  var _previousTaskWorkOrEmployee: TaskWorkOrEmployee = _

  @CustomShadowVariable(variableListenerClass = classOf[StartedAtUpdatingVariableListener],
    sources = Array(new PlanningVariableReference(variableName = "_previousTaskWorkOrEmployee")))
  var _startedAt: LocalDateTime = _

  def finishedAt: LocalDateTime = if (_startedAt == null) null else _startedAt.plusMinutes(task.duration)

  def this() = this(0L, Task(0, LocalDateTime.MIN, LocalDateTime.MIN, 0))

  def compareTo(other: TaskWork): Int = internalId compareTo other.internalId
}