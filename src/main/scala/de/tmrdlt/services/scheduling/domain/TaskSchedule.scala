package de.tmrdlt.services.scheduling.domain

import org.optaplanner.core.api.domain.solution.{PlanningEntityCollectionProperty, PlanningScore, PlanningSolution, ProblemFactCollectionProperty}
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore

import scala.jdk.CollectionConverters.SeqHasAsJava

@PlanningSolution
case class TaskSchedule(private val _assignees: Seq[Assignee],
                        private val _tasks: Seq[Task]) {

  @ValueRangeProvider(id = "assigneeRange")
  @ProblemFactCollectionProperty
  var assignees: java.util.List[Assignee] = _assignees.asJava

  @ValueRangeProvider(id = "taskRange")
  @PlanningEntityCollectionProperty
  var tasks: java.util.List[Task] = _tasks.asJava

  @PlanningScore
  var score: HardMediumSoftScore = _

  def this() = this(List(), List())
}
