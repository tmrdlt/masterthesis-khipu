package de.tmrdlt.database.workflowlistresource

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyDB.workflowListQuery
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListTable}
import de.tmrdlt.models.{GenericResourceEntity, WorkflowListResource}
import slick.ast.ColumnOption.Unique
import slick.lifted.{ForeignKeyQuery, Index, ProvenShape, Rep}
import slick.sql.SqlProfile.ColumnOption.{NotNull, Nullable}

import java.time.LocalDateTime

case class GenericResource(id: Long,
                           workflowListId: Long,
                           label: String,
                           value: Float,
                           createdAt: LocalDateTime,
                           updatedAt: LocalDateTime) extends WorkflowListResource {

  def toGenericResourceEntity: GenericResourceEntity =
    GenericResourceEntity(label = label, value = value)
}

class GenericResourceTable(tag: Tag)
  extends BaseTableLong[GenericResource](tag, "generic_resource") {

  def workflowListId: Rep[Long] = column[Long]("workflow_list_id", NotNull)

  def label: Rep[String] = column[String]("label", NotNull)

  def value: Rep[Float] = column[Float]("value", NotNull)

  def createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at", NotNull)

  def updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at", NotNull)

  def * : ProvenShape[GenericResource] = (
    id,
    workflowListId,
    label,
    value,
    createdAt,
    updatedAt
  ) <> (GenericResource.tupled, GenericResource.unapply)

  def workflowListForeignKey: ForeignKeyQuery[WorkflowListTable, WorkflowList] =
    foreignKey("workflow_list_fk", workflowListId, workflowListQuery)(_.id, onDelete = ForeignKeyAction.Cascade)

  def workflowListIdLabelIndex: Index = index(
    "generic_resource_workflow_list_id_label_unique_constraint",
    (workflowListId, label),
    unique = true
  )

}