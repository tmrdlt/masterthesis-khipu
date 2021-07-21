package de.tmrdlt.services

import de.tmrdlt.components.workflowlist.id.query.{WorkflowListColumnType, WorkflowListTemporalQuery}
import de.tmrdlt.models.{TemporalResourceEntity, WorkflowListType}
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
    "getBestExecutionOrderOfTasks" in {
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
      val result = workScheduleService.getBestExecutionOrderOfTasks(startDate, Seq(task1, task2, task3))

      result.executionOrder shouldBe Seq(task2.apiId, task3.apiId, task1.apiId)
      result.totalEndDate shouldBe LocalDateTime.of(2021, 7, 5, 18, 0)
      result.numberOfDueDatesFailed shouldBe 0
    }
  }
}
