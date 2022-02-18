package de.tmrdlt.models

import de.tmrdlt.constants.WorkflowListColumnType.WorkflowListColumnType
import de.tmrdlt.database.workschedule.{WorkSchedule, WorkScheduleJsonSupport}
import de.tmrdlt.models.WorkflowListType.WorkflowListType
import de.tmrdlt.services.scheduling.domain.Task
import spray.json.RootJsonFormat

import java.time.LocalDateTime

case class WorkflowListTemporal(id: Long,
                                apiId: String,
                                title: String,
                                workflowListType: WorkflowListType,
                                startDate: Option[LocalDateTime],
                                dueDate: Option[LocalDateTime],
                                duration: Long,
                                remainingDuration: Long,
                                inColumn: WorkflowListColumnType) {
  def toTask(now: LocalDateTime, workSchedule: WorkSchedule): Task = Task(
    id = id,
    apiId = apiId,
    title = title,
    now = now,
    workSchedule = workSchedule,
    startDate = startDate,
    dueDate = dueDate,
    duration = remainingDuration,
    inColumn = inColumn
  )
}

case class TaskPlanningSolution(id: Long,
                                apiId: String,
                                title: String,
                                startDate: Option[LocalDateTime],
                                dueDate: Option[LocalDateTime],
                                duration: Long,
                                startedAt: LocalDateTime,
                                finishedAt: LocalDateTime,
                                dueDateKept: Boolean,
                                index: Int)


case class TemporalQueryResultEntity(boardResult: TaskPlanningSolution,
                                     tasksResult: Seq[TaskPlanningSolution],
                                     workSchedule: WorkSchedule)


trait WorkflowListQueryJsonSupport extends JsonSupport with WorkScheduleJsonSupport {
  implicit val taskPlanningSolutionFormat: RootJsonFormat[TaskPlanningSolution] = jsonFormat10(TaskPlanningSolution)
  implicit val temporalQueryResultEntityFormat: RootJsonFormat[TemporalQueryResultEntity] = jsonFormat3(TemporalQueryResultEntity)

}
