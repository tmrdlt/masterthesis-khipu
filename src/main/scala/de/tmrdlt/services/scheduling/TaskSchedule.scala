package de.tmrdlt.services.scheduling

import org.optaplanner.core.api.domain.solution.{PlanningEntityCollectionProperty, PlanningScore, PlanningSolution, ProblemFactCollectionProperty}
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore

import scala.jdk.CollectionConverters._

@PlanningSolution
case class TaskSchedule(private val _tasks: List[Task], private val _taskWorks: List[TaskWork]) {

  @ProblemFactCollectionProperty
  @ValueRangeProvider(id = "tasksRange")
  var tasks: java.util.List[Task] = _tasks.asJava

  @PlanningEntityCollectionProperty
  @ValueRangeProvider(id = "tasksWorkRange")
  var tasksWorks: java.util.List[TaskWork] = _taskWorks.asJava

  @PlanningScore
  var score: HardSoftScore = _

  def this() = this(List(), List())
}