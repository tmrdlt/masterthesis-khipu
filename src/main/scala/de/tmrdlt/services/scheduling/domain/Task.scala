package de.tmrdlt.services.scheduling.domain

import de.tmrdlt.constants.WorkflowListColumnType
import de.tmrdlt.constants.WorkflowListColumnType.WorkflowListColumnType
import de.tmrdlt.database.workschedule.WorkSchedule
import de.tmrdlt.models.TaskPlanningSolution
import de.tmrdlt.services.scheduling.domain.solver.StartedAtUpdatingVariableListener
import de.tmrdlt.utils.WorkScheduleUtil
import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.lookup.PlanningId
import org.optaplanner.core.api.domain.variable._

import java.time.LocalDateTime
import scala.math.Ordered.orderingToOrdered

@PlanningEntity
case class Task(val id: Long,
                val apiId: String,
                val title: String,
                val now: LocalDateTime,
                val workSchedule: WorkSchedule,
                val startDate: Option[LocalDateTime],
                val dueDate: Option[LocalDateTime],
                val duration: Long,
                val inColumn: WorkflowListColumnType) extends TaskOrAssignee with Comparable[Task] {

  @PlanningId
  val internalId: Long = id

  @AnchorShadowVariable(sourceVariableName = "_previousTaskOrEmployee")
  var assignee: Assignee = _

  @PlanningVariable(graphType = PlanningVariableGraphType.CHAINED, valueRangeProviderRefs = Array("tasksWorkRange", "assigneeRange"))
  var _previousTaskOrEmployee: TaskOrAssignee = _

  @CustomShadowVariable(variableListenerClass = classOf[StartedAtUpdatingVariableListener],
    sources = Array(new PlanningVariableReference(variableName = "_previousTaskOrEmployee")))
  var _startedAt: LocalDateTime = _

  def finishedAt: LocalDateTime = if (_startedAt == null) {
    null
  } else {
    WorkScheduleUtil.getFinishDateRecursive(workSchedule, _startedAt, duration)
  }

  def this() = this(0L, "", "", LocalDateTime.now, WorkSchedule(0, 0, List(), Some(LocalDateTime.now())), Some(LocalDateTime.MIN), Some(LocalDateTime.MIN), 0, WorkflowListColumnType.OPEN)

  def compareTo(other: Task): Int = internalId compareTo other.internalId

  def toTaskPlanningSolution(index: Int): TaskPlanningSolution = TaskPlanningSolution(
    id = id,
    apiId = apiId,
    title = title,
    dueDate = dueDate,
    startDate = startDate,
    duration = duration,
    startedAt = _startedAt,
    finishedAt = finishedAt,
    dueDateKept = dueDate match {
      case Some(dueDate) => finishedAt <= dueDate
      case _ => true
    },
    index = index
  )
}
