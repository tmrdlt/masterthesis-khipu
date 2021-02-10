package de.tmrdlt.database.workflowlist

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.UsageType.UsageType
import de.tmrdlt.models.{UsageType, WorkflowListEntity}
import slick.lifted.{ForeignKeyQuery, Index, ProvenShape, Rep}
import slick.sql.SqlProfile.ColumnOption.{NotNull, Nullable}

import java.time.LocalDateTime
import java.util.UUID

/**
 * Database representation of a workflowList.
 *
 * @param id          Database id
 * @param uuid        UUID (generated upon insert) to be used as a unique identifier
 *                    when the real db id is not desired.
 * @param title       TBD
 * @param description TBD
 * @param parentId    TBD
 * @param order       TBD
 * @param createdAt   TBD
 * @param updatedAt   TBD
 */
case class WorkflowList(id: Long,
                        uuid: UUID,
                        title: String,
                        description: Option[String],
                        usageType: UsageType,
                        parentId: Option[Long],
                        order: Long,
                        createdAt: LocalDateTime,
                        updatedAt: LocalDateTime) {

  def toWorkflowListEntity(children: Seq[WorkflowListEntity], level: Long): WorkflowListEntity =
    WorkflowListEntity(
      id = id,
      uuid = uuid,
      title = title,
      description = description,
      children = children,
      usageType = usageType,
      level = level,
      createdAt = createdAt,
      updatedAt = updatedAt
    )
}

class WorkflowListTable(tag: Tag)
  extends BaseTableLong[WorkflowList](tag, "workflow_list") {

  def uuid: Rep[UUID] = column[UUID]("uuid", NotNull)

  def title: Rep[String] = column[String]("title", NotNull)

  def description: Rep[Option[String]] = column[Option[String]]("description", Nullable)

  def usageType: Rep[UsageType] = column[UsageType]("usage_type", Nullable)

  def parentId: Rep[Option[Long]] = column[Option[Long]]("parent_id", Nullable)

  def order: Rep[Long] = column[Long]("order", NotNull)

  def createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at", NotNull)

  def updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at", NotNull)

  def parentForeignKey: ForeignKeyQuery[WorkflowListTable, WorkflowList] =
    foreignKey("parent_fk", parentId, TableQuery[WorkflowListTable])(_.id.?, onDelete = ForeignKeyAction.Cascade)

  def sessionTokenIndex: Index = index("order_index", order, unique = true)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def * : ProvenShape[WorkflowList] =
    (id, uuid, title, description, usageType, parentId, order, createdAt, updatedAt).mapTo[WorkflowList]
}
