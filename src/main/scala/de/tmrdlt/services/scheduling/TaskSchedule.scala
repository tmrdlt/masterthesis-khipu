package de.tmrdlt.services.scheduling

import org.optaplanner.core.api.domain.solution.{PlanningEntityCollectionProperty, PlanningScore, PlanningSolution, ProblemFactCollectionProperty}
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore

import scala.jdk.CollectionConverters.SeqHasAsJava


@PlanningSolution
case class TaskSchedule(private val _employees: Seq[Assignee],
                        private val _tasks: Seq[Task]) {

  @ValueRangeProvider(id = "employeeRange")
  @ProblemFactCollectionProperty
  var employees: java.util.List[Assignee] = _employees.asJava

  @PlanningEntityCollectionProperty
  @ValueRangeProvider(id = "tasksWorkRange")
  var tasks: java.util.List[Task] = _tasks.asJava

  @PlanningScore
  var score: HardMediumSoftScore = _

  def this() = this(List(), List())
}