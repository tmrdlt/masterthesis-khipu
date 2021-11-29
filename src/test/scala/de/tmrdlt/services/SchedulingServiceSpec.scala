package de.tmrdlt.services

import de.tmrdlt.constants.WorkflowListColumnType
import de.tmrdlt.database.workschedule.WorkSchedule
import de.tmrdlt.models.{WorkflowListTemporal, WorkflowListType}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.{DayOfWeek, LocalDateTime}
import scala.annotation.nowarn

class SchedulingServiceSpec extends AnyWordSpec with Matchers {

  private val schedulingService = new SchedulingService
  private val now = LocalDateTime.of(2021, 6, 1, 8, 37)
  private val workSchedule = WorkSchedule(10, 18, List(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY), Some(now))
  val tasks = Seq(
    WorkflowListTemporal(
      id = 1L,
      apiId = "id1",
      title = "task1",
      workflowListType = WorkflowListType.ITEM,
      startDate = Some(LocalDateTime.of(2021, 6, 2, 9, 0)),
      dueDate = Some(LocalDateTime.of(2021, 6, 2, 16, 0)),
      duration = 360L,
      remainingDuration = 360L,
      inColumn = WorkflowListColumnType.OPEN),
    WorkflowListTemporal(
      id = 2L,
      apiId = "id2",
      title = "task2",
      workflowListType = WorkflowListType.ITEM,
      startDate = None,
      dueDate = Some(LocalDateTime.of(2021, 6, 1, 12, 0)),
      duration = 120L,
      remainingDuration = 120L,
      inColumn = WorkflowListColumnType.OPEN),
    WorkflowListTemporal(
      id = 3L,
      apiId = "id3",
      title = "task3",
      workflowListType = WorkflowListType.ITEM,
      startDate = None,
      dueDate = None,
      duration = 120L,
      remainingDuration = 60L,
      inColumn = WorkflowListColumnType.IN_PROGRESS),
    WorkflowListTemporal(
      id = 4L,
      apiId = "id4",
      title = "task4",
      workflowListType = WorkflowListType.ITEM,
      startDate = None,
      dueDate = None,
      duration = 480L,
      remainingDuration = 480L,
      inColumn = WorkflowListColumnType.OPEN))

  "scheduleTasks" should {
    "schedule tasks correctly" in {
      val result = schedulingService.scheduleTasks(now, workSchedule, tasks)
      result.map(_.id) shouldBe Seq(2L, 3L, 1L, 4L)
      result.last.finishedAt shouldBe LocalDateTime.of(2021, 6, 3, 16, 0)
      result.count(_.dueDateKept == false) shouldBe 0
    }
  }

  "scheduleTasksNaive" should {
    "schedule tasks correctly" in {
      @nowarn
      val result = schedulingService.scheduleTasksNaive(now, workSchedule, tasks)
      result.map(_.id) shouldBe Seq(2L, 3L, 1L, 4L)
      result.last.finishedAt shouldBe LocalDateTime.of(2021, 6, 3, 16, 0)
      result.count(_.dueDateKept == false) shouldBe 0
    }
  }
}
