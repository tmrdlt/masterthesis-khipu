package de.tmrdlt.models

import spray.json.RootJsonFormat

import java.time.LocalDateTime


case class WorkflowListExecution(apiId: String,
                                 title: String,
                                 duration: Long,
                                 endDate: LocalDateTime,
                                 dueDate: Option[LocalDateTime],
                                 dueDateKept: Boolean)

object WorkflowListsExecutionResult {
  implicit def ordering[A <: WorkflowListsExecutionResult]: Ordering[A] =
    Ordering.by(t => (t.numberOfDueDatesFailed, t.totalEndDate))
}

case class WorkflowListsExecutionResult(executionOrder: Seq[WorkflowListExecution],
                                        totalEndDate: LocalDateTime,
                                        numberOfDueDatesFailed: Int) // TODO To show in frontend make object, which contains wl ID and projected due date


case class TemporalQueryResultEntity(totalDurationMinutes: Long,
                                     bestExecutionResult: WorkflowListsExecutionResult)


trait WorkflowListQueryJsonSupport extends JsonSupport {
  implicit val workflowListExecutionFormat: RootJsonFormat[WorkflowListExecution] = jsonFormat6(WorkflowListExecution)
  implicit val workflowListsExecutionResultFormat: RootJsonFormat[WorkflowListsExecutionResult] = jsonFormat3(WorkflowListsExecutionResult.apply)
  implicit val temporalQueryResultEntityFormat: RootJsonFormat[TemporalQueryResultEntity] = jsonFormat2(TemporalQueryResultEntity)

}
