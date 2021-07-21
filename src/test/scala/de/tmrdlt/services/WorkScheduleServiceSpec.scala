package de.tmrdlt.services

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.LocalDateTime

class WorkScheduleServiceSpec extends AnyWordSpec with Matchers {

  private val workScheduleService = new WorkScheduleService

  private val startDate = LocalDateTime.of(2021, 7, 1, 11, 0)
  private val finishDate = LocalDateTime.of(2021, 7, 5, 11, 0)
  private val duration = 960

  "WorkScheduleService" must {
    "getFinishDateRecursive should calculate a correct finish date" in {
      workScheduleService.getFinishDateRecursive(startDate, duration) shouldBe finishDate
    }
    "getDurationInMinutesRecursive should calculate a correct duration" in {
      workScheduleService.getDurationInMinutesRecursive(startDate, finishDate) shouldBe duration
    }
  }
}
