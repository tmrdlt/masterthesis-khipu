package de.tmrdlt.models

import de.tmrdlt.models.UserType.UserType
import spray.json.{JsonFormat, RootJsonFormat}

import java.util.UUID

case class WorkflowListEntity(UUID: UUID,
                              children: Seq[WorkflowListEntity],
                              title: String,
                              description: Option[String]) {
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

trait WorkflowListJsonSupport extends JsonSupport {

  implicit val workflowListFormat: RootJsonFormat[WorkflowListEntity] =
    rootFormat(lazyFormat(jsonFormat(WorkflowListEntity, "uuid", "children", "title", "description")))

  implicit val createWorkflowListEntityFormat: RootJsonFormat[CreateWorkflowListEntity] = jsonFormat2(CreateWorkflowListEntity)
}