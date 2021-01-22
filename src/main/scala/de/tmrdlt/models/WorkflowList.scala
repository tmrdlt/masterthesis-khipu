package de.tmrdlt.models

import de.tmrdlt.models.UsageType.UsageType
import spray.json.RootJsonFormat

import java.time.LocalDateTime
import java.util.UUID

case class WorkflowListEntity(id: Long,
                              uuid: UUID,
                              title: String,
                              description: Option[String],
                              children: Seq[WorkflowListEntity],
                              usageType: UsageType,
                              createdAt: LocalDateTime,
                              updatedAt: LocalDateTime)

case class CreateWorkflowListEntity(title: String,
                                    description: Option[String])

case class UpdateWorkflowListEntity(parentId: Long) // TODO enhance with other fields

trait WorkflowListJsonSupport extends JsonSupport with UsageTypeJsonSupport {

  implicit val workflowListFormat: RootJsonFormat[WorkflowListEntity] =
    rootFormat(lazyFormat(jsonFormat(WorkflowListEntity,
      "id",
      "uuid",
      "title",
      "description",
      "children",
      "usageType",
      "createdAt",
      "updatedAt")))
  implicit val createWorkflowListEntityFormat: RootJsonFormat[CreateWorkflowListEntity] = jsonFormat2(CreateWorkflowListEntity)
  implicit val updateWorkflowListEntityFormat: RootJsonFormat[UpdateWorkflowListEntity] = jsonFormat1(UpdateWorkflowListEntity)
}