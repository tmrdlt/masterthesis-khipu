package de.tmrdlt.database.workflowlist

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyDB.workflowListQuery
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.database.workflowlistresource.{GenericResource, TemporalResource}
import de.tmrdlt.models.WorkflowListDataSource.WorkflowListDataSource
import de.tmrdlt.models.WorkflowListState.WorkflowListState
import de.tmrdlt.models.WorkflowListType.WorkflowListType
import de.tmrdlt.models.WorkflowListUseCase.WorkflowListUseCase
import de.tmrdlt.models.{WorkflowListEntity, WorkflowListSimpleEntity}
import slick.lifted.{ForeignKeyQuery, ProvenShape, Rep}
import slick.sql.SqlProfile.ColumnOption.{NotNull, Nullable}

import java.time.LocalDateTime

/**
 * Database representation of a workflowList.
 *
 * @param id          Database id
 * @param apiId       UUID (generated upon insert) to be used as a unique identifier
 *                    when the real db id is not desired.
 * @param title       TBD
 * @param description TBD
 * @param parentId    TBD
 * @param position    TBD
 * @param listType    TBD
 * @param state       TBD
 * @param useCase     TBD
 * @param dataSource  TBD
 * @param createdAt   TBD
 * @param updatedAt   TBD
 */
case class WorkflowList(id: Long,
                        apiId: String,
                        title: String,
                        description: Option[String],
                        parentId: Option[Long],
                        position: Long,
                        listType: WorkflowListType,
                        state: Option[WorkflowListState],
                        dataSource: WorkflowListDataSource,
                        useCase: Option[WorkflowListUseCase],
                        isTemporalConstraintBoard: Option[Boolean] = None,
                        ownerApiId: Option[String] = None,
                        createdAt: LocalDateTime,
                        updatedAt: LocalDateTime) {

  def toWorkflowListEntity(children: Seq[WorkflowListEntity],
                           level: Long,
                           workflowLists: Seq[WorkflowList],
                           temporalResources: Seq[TemporalResource],
                           genericResources: Seq[GenericResource]): WorkflowListEntity = {
    WorkflowListEntity(
      apiId = apiId,
      title = title,
      description = description,
      children = children.sortBy(_.position), // Important to return the workflow lists in a ordered way!
      usageType = listType,
      level = level,
      position = position,
      isTemporalConstraintBoard = isTemporalConstraintBoard.getOrElse(false),
      temporalResource = temporalResources.find(_.workflowListId == id).map(_.toTemporalResourceEntity(workflowLists)),
      genericResources = genericResources.filter(_.workflowListId == id).map(_.toGenericResourceEntity),
      createdAt = createdAt,
      updatedAt = updatedAt
    )
  }

  def toWorkflowListSimpleEntity: WorkflowListSimpleEntity =
    WorkflowListSimpleEntity(
      apiId = apiId,
      title = title
    )
}

class WorkflowListTable(tag: Tag)
  extends BaseTableLong[WorkflowList](tag, "workflow_list") {

  def apiId: Rep[String] = column[String]("api_id", NotNull)

  def title: Rep[String] = column[String]("title", NotNull)

  def description: Rep[Option[String]] = column[Option[String]]("description", Nullable)

  def parentId: Rep[Option[Long]] = column[Option[Long]]("parent_id", Nullable)

  def position: Rep[Long] = column[Long]("position", NotNull)

  def listType: Rep[WorkflowListType] = column[WorkflowListType]("list_type", NotNull)

  def state: Rep[Option[WorkflowListState]] = column[Option[WorkflowListState]]("state", Nullable)

  def dataSource: Rep[WorkflowListDataSource] = column[WorkflowListDataSource]("data_source", NotNull)

  def useCase: Rep[Option[WorkflowListUseCase]] = column[Option[WorkflowListUseCase]]("use_case", Nullable)

  def isTemporalConstraintBoard: Rep[Option[Boolean]] = column[Option[Boolean]]("is_temporal_constraint_board", Nullable)

  def ownerApiId: Rep[Option[String]] = column[Option[String]]("owner_api_id", Nullable)

  def createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at", NotNull)

  def updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at", NotNull)

  def parentForeignKey: ForeignKeyQuery[WorkflowListTable, WorkflowList] =
    foreignKey("parent_fk", parentId, workflowListQuery)(_.id.?, onDelete = ForeignKeyAction.Cascade)

  def * : ProvenShape[WorkflowList] = (
    id,
    apiId,
    title,
    description,
    parentId,
    position,
    listType,
    state,
    dataSource,
    useCase,
    isTemporalConstraintBoard,
    ownerApiId,
    createdAt,
    updatedAt
  ) <> (WorkflowList.tupled, WorkflowList.unapply)
}
