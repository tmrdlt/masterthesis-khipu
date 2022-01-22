package de.tmrldt.utils

import de.tmrdlt.database.workschedule.WorkSchedule
import de.tmrdlt.utils.WorkScheduleUtil
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.{DayOfWeek, LocalDateTime}

class WorkScheduleUtilSpec extends AnyWordSpec with Matchers {

  private val workSchedule = WorkSchedule(10, 18, List(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY), Some(LocalDateTime.now()))

  private val startDate = LocalDateTime.of(2021, 7, 1, 11, 0)
  private val finishDate = LocalDateTime.of(2021, 7, 5, 11, 0)
  private val duration = 960

  "getStartDateWithinWorkSchedule" should {
    "calculate correct startDate that corresponds to working schedule by given start date" in {
      val weekendStartDate = LocalDateTime.of(2021, 7, 3, 7, 0)
      val trueStartDate = LocalDateTime.of(2021, 7, 5, 10, 0)
      WorkScheduleUtil.getStartedAtWithinWorkSchedule(workSchedule, weekendStartDate) shouldBe trueStartDate
    }
  }
  "getFinishDateRecursive" should {
    "calculate correct finish date by given startDate and duration based on work schedule" in {
      WorkScheduleUtil.getFinishDateRecursive(workSchedule, startDate, duration) shouldBe finishDate
    }
  }
  "getDurationInMinutesRecursive" should {
    "calculate correct duration by given startDate and finishDate based on work schedule" in {
      WorkScheduleUtil.getDurationInMinutesRecursive(workSchedule, startDate, finishDate) shouldBe duration
    }
  }
}
