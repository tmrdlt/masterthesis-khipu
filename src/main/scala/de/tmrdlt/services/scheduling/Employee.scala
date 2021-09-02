package de.tmrdlt.services.scheduling

import org.optaplanner.core.api.domain.lookup.PlanningId

import java.time.LocalDateTime


case class Employee(val id: Long) extends TaskOrEmployee {

  @PlanningId
  val internalId: Long = id

  def this() = this(0L)

  @Override
  override def employee: Employee = this

  override def finishedAt: LocalDateTime = LocalDateTime.MIN

}
