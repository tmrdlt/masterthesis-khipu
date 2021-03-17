package de.tmrdlt.models

import de.tmrdlt.models.TemporalConstraintType.TemporalConstraintType
import de.tmrdlt.models.WorkflowListType.WorkflowListType
import spray.json.RootJsonFormat

import java.time.LocalDateTime

case class WorkflowListEntity(id: Long,
                              uuid: String,
                              title: String,
                              description: Option[String],
                              children: Seq[WorkflowListEntity],
                              usageType: WorkflowListType,
                              level: Long,
                              position: Long,
                              isTemporalConstraintBoard: Boolean,
                              temporalConstraint: Option[TemporalConstraintEntity],
                              createdAt: LocalDateTime,
                              updatedAt: LocalDateTime) // TODO add owner

case class CreateWorkflowListEntity(title: String,
                                    description: Option[String],
                                    listType: WorkflowListType,
                                    parentApiId: Option[String])

case class UpdateWorkflowListEntity(newTitle: String,
                                    newDescription: Option[String])

case class ConvertWorkflowListEntity(newListType: WorkflowListType)

case class MoveWorkflowListEntity(newParentApiId: Option[String],
                                  newPosition: Option[Long])

case class ReorderWorkflowListEntity(newPosition: Long)

case class TemporalConstraintEntity(temporalConstraintType: TemporalConstraintType,
                                    dueDate: Option[LocalDateTime],
                                    connectedWorkflowListId: Option[Long],
                                    createdAt: LocalDateTime,
                                    updatedAt: LocalDateTime)

trait WorkflowListJsonSupport extends JsonSupport with EnumJsonSupport {

  implicit val temporalConstraintEntityFormat: RootJsonFormat[TemporalConstraintEntity] = jsonFormat5(TemporalConstraintEntity)
  implicit val workflowListEntityFormat: RootJsonFormat[WorkflowListEntity] =
    rootFormat(lazyFormat(jsonFormat(WorkflowListEntity,
      "id",
      "uuid",
      "title",
      "description",
      "children",
      "usageType",
      "level",
      "position",
      "isTemporalConstraintBoard",
      "temporalConstraint",
      "createdAt",
      "updatedAt")))
  implicit val createWorkflowListEntityFormat: RootJsonFormat[CreateWorkflowListEntity] = jsonFormat4(CreateWorkflowListEntity)
  implicit val updateWorkflowListEntityFormat: RootJsonFormat[UpdateWorkflowListEntity] = jsonFormat2(UpdateWorkflowListEntity)
  implicit val convertWorkflowListEntityFormat: RootJsonFormat[ConvertWorkflowListEntity] = jsonFormat1(ConvertWorkflowListEntity)
  implicit val moveWorkflowListEntityFormat: RootJsonFormat[MoveWorkflowListEntity] = jsonFormat2(MoveWorkflowListEntity)
  implicit val reorderWorkflowListEntityFormat: RootJsonFormat[ReorderWorkflowListEntity] = jsonFormat1(ReorderWorkflowListEntity)

}