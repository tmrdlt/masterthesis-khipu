package de.tmrdlt.database.workflowlist

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyDB.workflowListQuery
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.WorkflowListDataSource.WorkflowListDataSource
import de.tmrdlt.models.WorkflowListType.WorkflowListType
import de.tmrdlt.models.WorkflowListEntity
import de.tmrdlt.models.WorkflowListState.WorkflowListState
import de.tmrdlt.models.WorkflowListUseCase.WorkflowListUseCase
import slick.lifted.{ForeignKeyQuery, ProvenShape, Rep}
import slick.sql.SqlProfile.ColumnOption.{NotNull, Nullable}

import java.time.LocalDateTime
import java.util.UUID

/**
 * Database representation of a workflowList.
 *
 * @param id               Database id
 * @param apiId            UUID (generated upon insert) to be used as a unique identifier
 *                         when the real db id is not desired.
 * @param title            TBD
 * @param description      TBD
 * @param parentId      TBD
 * @param position TBD
 * @param listType         TBD
 * @param state            TBD
 * @param useCase          TBD
 * @param dataSource       TBD
 * @param createdAt        TBD
 * @param updatedAt        TBD
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
                        createdAt: LocalDateTime,
                        updatedAt: LocalDateTime) {

  def toWorkflowListEntity(children: Seq[WorkflowListEntity], level: Long): WorkflowListEntity =
    WorkflowListEntity(
      id = id,
      uuid = apiId,
      title = title,
      description = description,
      children = children.sortBy(_.order), // Important to return the workflow lists in a ordered way!
      usageType = listType,
      level = level,
      order = position,
      createdAt = createdAt,
      updatedAt = updatedAt
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

  def createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at", NotNull)

  def updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at", NotNull)

  def parentForeignKey: ForeignKeyQuery[WorkflowListTable, WorkflowList] =
    foreignKey("parent_fk", parentId, workflowListQuery)(_.id.?, onDelete = ForeignKeyAction.Cascade)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def * : ProvenShape[WorkflowList] =
    (id, apiId, title, description, parentId, position, listType, state, dataSource, useCase, createdAt, updatedAt).mapTo[WorkflowList]
}
