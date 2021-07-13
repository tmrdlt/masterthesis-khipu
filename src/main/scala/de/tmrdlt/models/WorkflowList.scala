package de.tmrdlt.models

import de.tmrdlt.models.WorkflowListType.WorkflowListType
import spray.json.RootJsonFormat

import java.time.LocalDateTime

case class WorkflowListEntity(apiId: String,
                              title: String,
                              description: Option[String],
                              children: Seq[WorkflowListEntity],
                              usageType: WorkflowListType,
                              level: Long,
                              position: Long,
                              isTemporalConstraintBoard: Boolean,
                              temporalResource: Option[TemporalResourceEntity],
                              genericResources: Seq[NumericResourceEntity],
                              createdAt: LocalDateTime,
                              updatedAt: LocalDateTime) // TODO add owner

case class CreateWorkflowListEntity(title: String,
                                    description: Option[String],
                                    listType: WorkflowListType,
                                    parentApiId: Option[String],
                                    userApiId: String)

case class UpdateWorkflowListEntity(newTitle: String,
                                    newDescription: Option[String],
                                    isTemporalConstraintBoard: Option[Boolean])

case class ConvertWorkflowListEntity(newListType: WorkflowListType)

case class MoveWorkflowListEntity(newParentApiId: Option[String],
                                  newPosition: Option[Long],
                                  userApiId: String)

case class ReorderWorkflowListEntity(newPosition: Long)

case class WorkflowListSimpleEntity(apiId: String,
                                    title: String)

trait WorkflowListJsonSupport extends JsonSupport with EnumJsonSupport with WorkflowListResourceJsonSupport {

  implicit val workflowListSimpleEntityFormat: RootJsonFormat[WorkflowListSimpleEntity] = jsonFormat2(WorkflowListSimpleEntity)
  implicit val workflowListEntityFormat: RootJsonFormat[WorkflowListEntity] =
    rootFormat(lazyFormat(jsonFormat(WorkflowListEntity,
      "apiId",
      "title",
      "description",
      "children",
      "usageType",
      "level",
      "position",
      "isTemporalConstraintBoard",
      "temporalResource",
      "genericResources",
      "createdAt",
      "updatedAt")))
  implicit val createWorkflowListEntityFormat: RootJsonFormat[CreateWorkflowListEntity] = jsonFormat5(CreateWorkflowListEntity)
  implicit val updateWorkflowListEntityFormat: RootJsonFormat[UpdateWorkflowListEntity] = jsonFormat3(UpdateWorkflowListEntity)
  implicit val convertWorkflowListEntityFormat: RootJsonFormat[ConvertWorkflowListEntity] = jsonFormat1(ConvertWorkflowListEntity)
  implicit val moveWorkflowListEntityFormat: RootJsonFormat[MoveWorkflowListEntity] = jsonFormat3(MoveWorkflowListEntity)
  implicit val reorderWorkflowListEntityFormat: RootJsonFormat[ReorderWorkflowListEntity] = jsonFormat1(ReorderWorkflowListEntity)

}