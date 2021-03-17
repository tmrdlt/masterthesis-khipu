package de.tmrdlt.database.temporalcontraint

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.TemporalConstraintType.TemporalConstraintType
import de.tmrdlt.models.{EnumJsonSupport, JsonSupport, TemporalConstraintEntity}
import slick.lifted.{ProvenShape, Rep}
import slick.sql.SqlProfile.ColumnOption.{NotNull, Nullable}
import spray.json.RootJsonFormat

import java.time.LocalDateTime

case class TemporalConstraint(id: Long,
                              workflowListId: Long,
                              temporalConstraintType: TemporalConstraintType,
                              dueDate: Option[LocalDateTime],
                              connectedWorkflowListId: Option[Long],
                              createdAt: LocalDateTime,
                              updatedAt: LocalDateTime) {
  def toTemporalConstraintEntity: TemporalConstraintEntity =
    TemporalConstraintEntity(
      temporalConstraintType = temporalConstraintType,
      dueDate = dueDate,
      connectedWorkflowListId = connectedWorkflowListId,
      createdAt = createdAt,
      updatedAt = updatedAt
    )
}

class TemporalConstraintTable(tag: Tag)
  extends BaseTableLong[TemporalConstraint](tag, "temporal_constraint") {

  def workflowListId: Rep[Long] = column[Long]("workflow_list_id", NotNull)

  def temporalConstraintType: Rep[TemporalConstraintType] = column[TemporalConstraintType]("temporal_constraint_type", NotNull)

  def dueDate: Rep[Option[LocalDateTime]] = column[Option[LocalDateTime]]("due_date", Nullable)

  def connectedWorkflowListId: Rep[Option[Long]] = column[Option[Long]]("connected_workflow_list_id", Nullable)

  def createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at", NotNull)

  def updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at", NotNull)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def * : ProvenShape[TemporalConstraint] =
    (id, workflowListId, temporalConstraintType, dueDate, connectedWorkflowListId, createdAt, updatedAt).mapTo[TemporalConstraint]
}