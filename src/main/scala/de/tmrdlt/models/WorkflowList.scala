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
                                    description: Option[String],
                                    parentUuid: Option[UUID])

case class UpdateWorkflowListEntity(newTitle: Option[String],
                                    newDescription: Option[String],
                                    newParentId: Option[Long])

case class ConvertWorkflowListEntity(convertTo: UsageType)

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
  implicit val createWorkflowListEntityFormat: RootJsonFormat[CreateWorkflowListEntity] = jsonFormat3(CreateWorkflowListEntity)
  implicit val updateWorkflowListEntityFormat: RootJsonFormat[UpdateWorkflowListEntity] = jsonFormat3(UpdateWorkflowListEntity)
  implicit val convertWorkflowListEntityFormat: RootJsonFormat[ConvertWorkflowListEntity] = jsonFormat1(ConvertWorkflowListEntity)
}