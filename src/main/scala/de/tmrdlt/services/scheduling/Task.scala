package de.tmrdlt.services.scheduling

import de.tmrdlt.components.solver.TaskPlanningSolution
import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.lookup.PlanningId
import org.optaplanner.core.api.domain.variable._

import java.time.LocalDateTime
import scala.math.Ordered.orderingToOrdered

@PlanningEntity
case class Task(val id: Long,
                val startDate: LocalDateTime,
                val dueDate: LocalDateTime,
                val duration: Long) extends TaskOrEmployee with Comparable[Task] {

  @PlanningId
  val internalId: Long = id

  @AnchorShadowVariable(sourceVariableName = "_previousTaskOrEmployee")
  var employee: Employee = _

  @PlanningVariable(graphType = PlanningVariableGraphType.CHAINED, valueRangeProviderRefs = Array("tasksWorkRange", "employeeRange"))
  var _previousTaskOrEmployee: TaskOrEmployee = _

  @CustomShadowVariable(variableListenerClass = classOf[StartedAtUpdatingVariableListener],
    sources = Array(new PlanningVariableReference(variableName = "_previousTaskOrEmployee")))
  var _startedAt: LocalDateTime = _

  def finishedAt: LocalDateTime = if (_startedAt == null) null else _startedAt.plusMinutes(duration)

  def this() = this(0L, LocalDateTime.MIN, LocalDateTime.MIN, 0)

  def compareTo(other: Task): Int = internalId compareTo other.internalId

  def toTaskPlanningSolution: TaskPlanningSolution = TaskPlanningSolution(
    id = id,
    startedAt = _startedAt,
    finishedAt = finishedAt,
    dueDateKept = finishedAt <= dueDate
  )
}