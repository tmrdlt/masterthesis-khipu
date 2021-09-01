package de.tmrdlt.services.scheduling

import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.lookup.PlanningId
import org.optaplanner.core.api.domain.variable._

import java.time.LocalDateTime

@PlanningEntity
case class Task(val id: Long, val startDate: LocalDateTime, val dueDate: LocalDateTime, val duration: Long) extends Comparable[Task] {

  @PlanningId
  val internalId: Long = id

  @PlanningVariable(graphType = PlanningVariableGraphType.CHAINED, valueRangeProviderRefs = Array("tasksRange"))
  var _previousTask: Task = _

  @InverseRelationShadowVariable(sourceVariableName = "_previousTask")
  var _nextTask: Task = _

  @CustomShadowVariable(variableListenerClass = classOf[StartedAtUpdatingVariableListener],
    sources = Array(new PlanningVariableReference(variableName = "_previousTask")))
  var _startedAt: LocalDateTime = _

  var _finishedAt: LocalDateTime = _


  def this() = this(0, LocalDateTime.MIN, LocalDateTime.MIN, 0)

  def compareTo(other: Task): Int = internalId compareTo other.internalId
}
