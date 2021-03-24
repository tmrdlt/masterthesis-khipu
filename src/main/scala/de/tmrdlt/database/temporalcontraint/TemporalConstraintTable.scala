package de.tmrdlt.database.temporalcontraint

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyDB.workflowListQuery
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListTable}
import de.tmrdlt.models.TemporalConstraintEntity
import de.tmrdlt.models.TemporalConstraintType.TemporalConstraintType
import slick.lifted.{ForeignKeyQuery, ProvenShape, Rep}
import slick.ast.ColumnOption.Unique
import slick.sql.SqlProfile.ColumnOption.{NotNull, Nullable}

import java.time.LocalDateTime

case class TemporalConstraint(id: Long,
                              workflowListId: Long,
                              temporalConstraintType: TemporalConstraintType,
                              dueDate: Option[LocalDateTime],
                              connectedWorkflowListId: Option[Long],
                              createdAt: LocalDateTime,
                              updatedAt: LocalDateTime) {

  def toTemporalConstraintEntity (workflowLists: Seq[WorkflowList]): TemporalConstraintEntity =
    TemporalConstraintEntity(
      temporalConstraintType = temporalConstraintType,
      dueDate = dueDate,
      connectedWorkflowListApiId = workflowLists.find(wl => connectedWorkflowListId.contains(wl.id)).map(_.apiId)
    )
}

class TemporalConstraintTable(tag: Tag)
  extends BaseTableLong[TemporalConstraint](tag, "temporal_constraint") {

  def workflowListId: Rep[Long] = column[Long]("workflow_list_id", NotNull, Unique)

  def temporalConstraintType: Rep[TemporalConstraintType] = column[TemporalConstraintType]("temporal_constraint_type", NotNull)

  def dueDate: Rep[Option[LocalDateTime]] = column[Option[LocalDateTime]]("due_date", Nullable)

  def connectedWorkflowListId: Rep[Option[Long]] = column[Option[Long]]("connected_workflow_list_id", Nullable)

  def createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at", NotNull)

  def updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at", NotNull)

  def workflowListForeignKey: ForeignKeyQuery[WorkflowListTable, WorkflowList] =
    foreignKey("workflow_list_fk", workflowListId, workflowListQuery)(_.id, onDelete = ForeignKeyAction.Cascade)

  def connectedWorkflowListForeignKey: ForeignKeyQuery[WorkflowListTable, WorkflowList] =
    foreignKey("connected_workflow_list_fk", connectedWorkflowListId, workflowListQuery)(_.id.?, onDelete = ForeignKeyAction.Cascade)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def * : ProvenShape[TemporalConstraint] =
    (id, workflowListId, temporalConstraintType, dueDate, connectedWorkflowListId, createdAt, updatedAt).mapTo[TemporalConstraint]
}