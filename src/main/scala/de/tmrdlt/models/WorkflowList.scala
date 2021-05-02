package de.tmrdlt.models

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
                                    parentApiId: Option[String],
                                    username: String)

case class UpdateWorkflowListEntity(newTitle: String,
                                    newDescription: Option[String],
                                    isTemporalConstraintBoard: Option[Boolean])

case class ConvertWorkflowListEntity(newListType: WorkflowListType)

case class MoveWorkflowListEntity(newParentApiId: Option[String],
                                  newPosition: Option[Long],
                                  username: String)

case class ReorderWorkflowListEntity(newPosition: Long)

case class WorkflowListSimpleEntity(apiId: String,
                                    title: String)

// TODO Could lead to problems when working with frontends from different timezones as we use LocalDateTime here
case class TemporalConstraintEntity(startDate: Option[LocalDateTime],
                                    endDate: Option[LocalDateTime],
                                    durationInMinutes: Option[Long],
                                    connectedWorkflowListApiId: Option[String])

trait WorkflowListJsonSupport extends JsonSupport with EnumJsonSupport {

  implicit val workflowListSimpleEntityFormat: RootJsonFormat[WorkflowListSimpleEntity] = jsonFormat2(WorkflowListSimpleEntity)
  implicit val temporalConstraintEntityFormat: RootJsonFormat[TemporalConstraintEntity] = jsonFormat4(TemporalConstraintEntity)
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
  implicit val createWorkflowListEntityFormat: RootJsonFormat[CreateWorkflowListEntity] = jsonFormat5(CreateWorkflowListEntity)
  implicit val updateWorkflowListEntityFormat: RootJsonFormat[UpdateWorkflowListEntity] = jsonFormat3(UpdateWorkflowListEntity)
  implicit val convertWorkflowListEntityFormat: RootJsonFormat[ConvertWorkflowListEntity] = jsonFormat1(ConvertWorkflowListEntity)
  implicit val moveWorkflowListEntityFormat: RootJsonFormat[MoveWorkflowListEntity] = jsonFormat3(MoveWorkflowListEntity)
  implicit val reorderWorkflowListEntityFormat: RootJsonFormat[ReorderWorkflowListEntity] = jsonFormat1(ReorderWorkflowListEntity)

}