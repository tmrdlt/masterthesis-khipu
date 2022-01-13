package de.tmrdlt.services.scheduling.domain

import org.optaplanner.core.api.domain.lookup.PlanningId

import java.time.LocalDateTime

case class Assignee(id: Long) extends TaskOrAssignee {

  @PlanningId
  val internalId: Long = id

  def this() = this(0L)

  def assignee: Assignee = this

  def finishedAt: LocalDateTime = LocalDateTime.MIN

}
