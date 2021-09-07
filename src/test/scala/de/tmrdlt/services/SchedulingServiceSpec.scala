package de.tmrdlt.services

import de.tmrdlt.components.workflowlist.id.query.{WorkflowListColumnType, WorkflowListTemporalQuery}
import de.tmrdlt.models.{TemporalResourceEntity, WorkflowListType}
import de.tmrdlt.services.scheduling.domain
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.LocalDateTime

class SchedulingServiceSpec extends AnyWordSpec with Matchers {

  private val schedulingService = new SchedulingService
  private val now = LocalDateTime.of(2021, 7, 1, 11, 0)

  "scheduleTasks" should {
    "schedule tasks correctly" in {
      val tasks = List(
        domain.Task(
          id = 1L,
          now = now,
          startDate = Some(LocalDateTime.of(2021, 7, 5, 10, 0)),
          dueDate = Some(LocalDateTime.of(2021, 7, 5, 18, 0)),
          duration = 480),
        domain.Task(
          id = 2L,
          now = now,
          startDate = None,
          dueDate = Some(LocalDateTime.of(2021, 7, 1, 13, 0)),
          duration = 60),
        domain.Task(
          id = 3L,
          now = now,
          startDate = None,
          dueDate = None,
          duration = 120),
      )
      val result = schedulingService.scheduleTasks(now, tasks)
      result.map(_.id) shouldBe Seq(2L, 3L, 1L)
      result.last.finishedAt shouldBe LocalDateTime.of(2021, 7, 5, 18, 0)
      result.count(_.dueDateKept == false) shouldBe 0
    }
  }

  "scheduleTasksNaive" should {
    "schedule tasks correctly" in {
      val task1 = WorkflowListTemporalQuery(
        "id1",
        "task1",
        Some(TemporalResourceEntity(
          Some(LocalDateTime.of(2021, 7, 5, 10, 0)),
          Some(LocalDateTime.of(2021, 7, 5, 18, 0)),
          Some(480)
        )),
        WorkflowListType.ITEM,
        columnType = WorkflowListColumnType.OPEN,
        480
      )
      val task2 = WorkflowListTemporalQuery(
        "id2",
        "task2",
        Some(TemporalResourceEntity(
          None,
          Some(LocalDateTime.of(2021, 7, 1, 13, 0)),
          Some(120)
        )),
        WorkflowListType.ITEM,
        columnType = WorkflowListColumnType.OPEN,
        120
      )
      val task3 = WorkflowListTemporalQuery(
        "id3",
        "task3",
        Some(TemporalResourceEntity(
          None,
          None,
          Some(120)
        )), WorkflowListType.ITEM,
        columnType = WorkflowListColumnType.IN_PROGRESS,
        60
      )
      val result = schedulingService.scheduleTasksNaive(now, Seq(task1, task2, task3))

      result.executionOrder.map(_.apiId) shouldBe Seq(task2.apiId, task3.apiId, task1.apiId)
      result.totalEndDate shouldBe LocalDateTime.of(2021, 7, 5, 18, 0)
      result.numberOfDueDatesFailed shouldBe 0
    }
  }
}
