package de.tmrdlt.models

import spray.json.RootJsonFormat

import java.time.LocalDateTime


case class WorkflowListExecution(apiId: String,
                                 title: String,
                                 endDate: LocalDateTime,
                                 dueDate: LocalDateTime,
                                 dueDateKept: Boolean)

object WorkflowListsExecutionResult {
  implicit def ordering[A <: WorkflowListsExecutionResult]: Ordering[A] =
    Ordering.by(t => (t.numberOfDueDatesFailed, t.totalEndDate))
}

case class WorkflowListsExecutionResult(executionOrder: Seq[String],
                                        totalEndDate: LocalDateTime,
                                        numberOfDueDatesFailed: Int) // TODO To show in frontend make object, which contains wl ID and projected due date


case class TemporalQueryResultEntity(totalDurationMinutes: Long,
                                     bestExecutionResult: WorkflowListsExecutionResult)


trait WorkflowListQueryJsonSupport extends JsonSupport {
  implicit val workflowListExecutionFormat: RootJsonFormat[WorkflowListExecution] = jsonFormat5(WorkflowListExecution)
  //implicit val workflowListsExecutionResultFormat: RootJsonFormat[WorkflowListsExecutionResult] = jsonFormat3(WorkflowListsExecutionResult)
  //implicit val temporalQueryResultEntityFormat: RootJsonFormat[TemporalQueryResultEntity] = jsonFormat1(TemporalQueryResultEntity)

}
