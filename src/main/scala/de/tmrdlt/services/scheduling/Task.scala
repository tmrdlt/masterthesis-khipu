package de.tmrdlt.services.scheduling

import java.time.LocalDateTime

case class Task(val id: Long, val startDate: LocalDateTime, val dueDate: LocalDateTime, val duration: Long) {
  def this() = this(0, LocalDateTime.MIN, LocalDateTime.MIN, 0)
}
