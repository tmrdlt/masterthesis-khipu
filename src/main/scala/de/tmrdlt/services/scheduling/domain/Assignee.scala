package de.tmrdlt.services.scheduling.domain

import org.optaplanner.core.api.domain.lookup.PlanningId

import java.time.LocalDateTime

case class Assignee(val id: Long) extends TaskOrAssignee {

  @PlanningId
  val internalId: Long = id

  def this() = this(0L)

  @Override
  override def assignee: Assignee = this

  override def finishedAt: LocalDateTime = LocalDateTime.MIN

}
