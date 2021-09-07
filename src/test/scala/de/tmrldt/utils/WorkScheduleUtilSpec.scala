package de.tmrldt.utils

import de.tmrdlt.utils.WorkScheduleUtil
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.LocalDateTime

class WorkScheduleUtilSpec extends AnyWordSpec with Matchers {

  private val startDate = LocalDateTime.of(2021, 7, 1, 11, 0)
  private val finishDate = LocalDateTime.of(2021, 7, 5, 11, 0)
  private val duration = 960

  "getFinishDateRecursive" should {
    "calculate correct finish date by given startDate and duration based on work schedule" in {
      WorkScheduleUtil.getFinishDateRecursive(startDate, duration) shouldBe finishDate
    }
  }
  "getDurationInMinutesRecursive" should {
    "calculate correct duration by given startDate and finishDate based on work schedule" in {
      WorkScheduleUtil.getDurationInMinutesRecursive(startDate, finishDate) shouldBe duration
    }
  }
}
