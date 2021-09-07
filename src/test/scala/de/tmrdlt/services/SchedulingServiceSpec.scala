package de.tmrdlt.services

import de.tmrdlt.constants.WorkflowListColumnType
import de.tmrdlt.models.{WorkflowListTemporal, WorkflowListType}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.LocalDateTime

class SchedulingServiceSpec extends AnyWordSpec with Matchers {

  private val schedulingService = new SchedulingService
  private val now = LocalDateTime.of(2021, 7, 1, 11, 0)
  val tasks = Seq(
    WorkflowListTemporal(
      id = 1L,
      apiId = "id1",
      title = "task1",
      workflowListType = WorkflowListType.ITEM,
      startDate = Some(LocalDateTime.of(2021, 7, 5, 10, 0)),
      dueDate = Some(LocalDateTime.of(2021, 7, 5, 18, 0)),
      duration = 480L,
      remainingDuration = 480L,
      inColumn = WorkflowListColumnType.OPEN),
    WorkflowListTemporal(
      id = 2L,
      apiId = "id2",
      title = "task2",
      workflowListType = WorkflowListType.ITEM,
      startDate = None,
      dueDate = Some(LocalDateTime.of(2021, 7, 1, 13, 0)),
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
      inColumn = WorkflowListColumnType.IN_PROGRESS))

  "scheduleTasks" should {
    "schedule tasks correctly" in {
      val result = schedulingService.scheduleTasks(now, tasks)
      result.map(_.id) shouldBe Seq(2L, 3L, 1L)
      result.last.finishedAt shouldBe LocalDateTime.of(2021, 7, 5, 18, 0)
      result.count(_.dueDateKept == false) shouldBe 0
    }
  }

  "scheduleTasksNaive" should {
    "schedule tasks correctly" in {
      val result = schedulingService.scheduleTasksNaive(now, tasks)
      result.map(_.id) shouldBe Seq(2L, 3L, 1L)
      result.last.finishedAt shouldBe LocalDateTime.of(2021, 7, 5, 18, 0)
      result.count(_.dueDateKept == false) shouldBe 0
    }
  }
}
