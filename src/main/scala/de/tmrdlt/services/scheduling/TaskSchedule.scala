package de.tmrdlt.services.scheduling

import org.optaplanner.core.api.domain.solution.{PlanningEntityCollectionProperty, PlanningScore, PlanningSolution, ProblemFactCollectionProperty}
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore

import scala.jdk.CollectionConverters._

@PlanningSolution
case class TaskSchedule(private val _employees: List[Employee],
                        private val _tasks: List[Task]) {

  @ValueRangeProvider(id = "employeeRange")
  @ProblemFactCollectionProperty
  var employees: java.util.List[Employee] = _employees.asJava

  @PlanningEntityCollectionProperty
  @ValueRangeProvider(id = "tasksWorkRange")
  var tasks: java.util.List[Task] = _tasks.asJava

  @PlanningScore
  var score: HardMediumSoftScore = _

  def this() = this(List(), List())
}