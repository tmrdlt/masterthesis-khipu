package de.tmrdlt.models

import de.tmrdlt.database.user.User
import de.tmrdlt.database.workflowlist.WorkflowList
import de.tmrdlt.database.workflowlistresource.{NumericResource, TemporalResource, TextualResource, UserResource}
import de.tmrdlt.models.WorkflowListType.WorkflowListType
import spray.json.RootJsonFormat

import java.time.LocalDateTime

case class WorkflowListsData(workflowLists: Seq[WorkflowList],
                             temporalResources: Seq[TemporalResource],
                             numericResources: Seq[NumericResource],
                             textualResources: Seq[TextualResource],
                             userResources: Seq[UserResource],
                             users: Seq[User])

case class WorkflowListEntity(id: Long,
                              apiId: String,
                              title: String,
                              description: Option[String],
                              children: Seq[WorkflowListEntity],
                              usageType: WorkflowListType,
                              level: Long,
                              position: Long,
                              isTemporalConstraintBoard: Boolean,
                              temporalResource: Option[TemporalResourceEntity],
                              userResource: Option[UserResourceEntity],
                              numericResources: Seq[NumericResourceEntity],
                              textualResources: Seq[TextualResourceEntity],
                              createdAt: LocalDateTime,
                              updatedAt: LocalDateTime) {
  def getStartDate: Option[LocalDateTime] = {
    temporalResource.flatMap(_.startDate)
  }

  def getEndDate: Option[LocalDateTime] = {
    temporalResource.flatMap(_.dueDate)
  }

  def getDuration: Option[Long] = {
    temporalResource.flatMap(_.durationInMinutes)
  }
}


case class CreateWorkflowListEntity(title: String,
                                    description: Option[String],
                                    listType: WorkflowListType,
                                    parentApiId: Option[String],
                                    isTemporalConstraintBoard: Option[Boolean],
                                    children: Seq[CreateWorkflowListEntity])

case class UpdateWorkflowListEntity(newTitle: String,
                                    newDescription: Option[String],
                                    isTemporalConstraintBoard: Option[Boolean])

case class ConvertWorkflowListEntity(newListType: WorkflowListType)

case class MoveWorkflowListEntity(newParentApiId: Option[String],
                                  newPosition: Option[Long])

case class ReorderWorkflowListEntity(newPosition: Long)

case class WorkflowListSimpleEntity(apiId: String,
                                    title: String)

trait WorkflowListJsonSupport extends JsonSupport with EnumJsonSupport with WorkflowListResourceJsonSupport {

  implicit val workflowListSimpleEntityFormat: RootJsonFormat[WorkflowListSimpleEntity] = jsonFormat2(WorkflowListSimpleEntity)
  implicit val workflowListEntityFormat: RootJsonFormat[WorkflowListEntity] =
    rootFormat(lazyFormat(jsonFormat(WorkflowListEntity,
      "id",
      "apiId",
      "title",
      "description",
      "children",
      "usageType",
      "level",
      "position",
      "isTemporalConstraintBoard",
      "temporalResource",
      "userResource",
      "numericResources",
      "textualResources",
      "createdAt",
      "updatedAt")))
  implicit val createWorkflowListEntityFormat: RootJsonFormat[CreateWorkflowListEntity] =
    rootFormat(lazyFormat(jsonFormat(CreateWorkflowListEntity,
      "title",
      "description",
      "listType",
      "parentApiId",
      "isTemporalConstraintBoard",
      "children")))
  implicit val updateWorkflowListEntityFormat: RootJsonFormat[UpdateWorkflowListEntity] = jsonFormat3(UpdateWorkflowListEntity)
  implicit val convertWorkflowListEntityFormat: RootJsonFormat[ConvertWorkflowListEntity] = jsonFormat1(ConvertWorkflowListEntity)
  implicit val moveWorkflowListEntityFormat: RootJsonFormat[MoveWorkflowListEntity] = jsonFormat2(MoveWorkflowListEntity)
  implicit val reorderWorkflowListEntityFormat: RootJsonFormat[ReorderWorkflowListEntity] = jsonFormat1(ReorderWorkflowListEntity)

}