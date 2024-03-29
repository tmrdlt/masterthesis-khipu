package de.tmrdlt.database.workflowlistresource

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyDB.workflowListQuery
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListTable}
import de.tmrdlt.models.{WorkflowListResource, TemporalResourceEntity}
import slick.ast.ColumnOption.Unique
import slick.lifted.{ForeignKeyQuery, ProvenShape, Rep}
import slick.sql.SqlProfile.ColumnOption.{NotNull, Nullable}

import java.time.LocalDateTime

case class TemporalResource(id: Long,
                            workflowListId: Long,
                            startDate: Option[LocalDateTime],
                            dueDate: Option[LocalDateTime],
                            durationInMinutes: Option[Long],
                            createdAt: LocalDateTime,
                            updatedAt: LocalDateTime) extends WorkflowListResource {

  def toTemporalResourceEntity: TemporalResourceEntity =
    TemporalResourceEntity(
      startDate = startDate,
      dueDate = dueDate,
      durationInMinutes = durationInMinutes
    )
}

class TemporalResourceTable(tag: Tag)
  extends BaseTableLong[TemporalResource](tag, "temporal_resource") {

  def workflowListId: Rep[Long] = column[Long]("workflow_list_id", NotNull, Unique)

  def startDate: Rep[Option[LocalDateTime]] = column[Option[LocalDateTime]]("start_date", Nullable)

  def dueDate: Rep[Option[LocalDateTime]] = column[Option[LocalDateTime]]("due_date", Nullable)

  def durationInMinutes: Rep[Option[Long]] = column[Option[Long]]("duration_in_minutes", Nullable)

  def createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at", NotNull)

  def updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at", NotNull)

  def workflowListForeignKey: ForeignKeyQuery[WorkflowListTable, WorkflowList] =
    foreignKey("workflow_list_fk", workflowListId, workflowListQuery)(_.id, onDelete = ForeignKeyAction.Cascade)

  def * : ProvenShape[TemporalResource] = (
    id,
    workflowListId,
    startDate, dueDate,
    durationInMinutes,
    createdAt,
    updatedAt
  ) <> (TemporalResource.tupled, TemporalResource.unapply)
}