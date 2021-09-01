package de.tmrdlt.services.scheduling

import org.optaplanner.core.api.domain.solution.{PlanningEntityCollectionProperty, PlanningScore, PlanningSolution, ProblemFactCollectionProperty}
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore

import scala.jdk.CollectionConverters._

@PlanningSolution
case class TaskSchedule(private val _tasks: List[Task], private val _tasksVariables: List[Task]) {

  @ProblemFactCollectionProperty
  @ValueRangeProvider(id = "tasksRange")
  var tasksVariables: java.util.List[Task] = _tasksVariables.asJava

  @PlanningEntityCollectionProperty
  var tasks: java.util.List[Task] = _tasks.asJava

  @PlanningScore
  var score: HardSoftScore = _

  def this() = this(List(), List())
}