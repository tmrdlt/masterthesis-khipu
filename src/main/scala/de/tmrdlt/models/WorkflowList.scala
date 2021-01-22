package de.tmrdlt.models

import de.tmrdlt.models.UserType.UserType
import spray.json.{JsonFormat, RootJsonFormat}

import java.time.LocalDateTime
import java.util.UUID

case class WorkflowListEntity(id: Long,
                              uuid: UUID,
                              title: String,
                              description: Option[String],
                              children: Seq[WorkflowListEntity],
                              createdAt: LocalDateTime,
                              updatedAt: LocalDateTime
                             ) {
  def getUserType: UserType =
    if (children.isEmpty) {
      UserType.TASK
    } else if (children.forall(l => l.children.isEmpty)) {
      UserType.LIST
    } else {
      UserType.PROJECT
    }
}

case class CreateWorkflowListEntity(title: String,
                                    description: Option[String])

case class UpdateWorkflowListEntity(parentId: Long) // TODO enhance with other fields

trait WorkflowListJsonSupport extends JsonSupport {

  implicit val workflowListFormat: RootJsonFormat[WorkflowListEntity] =
    rootFormat(lazyFormat(jsonFormat(WorkflowListEntity,
      "id",
      "uuid",
      "title",
      "description",
      "children",
      "createdAt",
      "updatedAt")))
  implicit val createWorkflowListEntityFormat: RootJsonFormat[CreateWorkflowListEntity] = jsonFormat2(CreateWorkflowListEntity)
  implicit val updateWorkflowListEntityFormat: RootJsonFormat[UpdateWorkflowListEntity] = jsonFormat1(UpdateWorkflowListEntity)
}