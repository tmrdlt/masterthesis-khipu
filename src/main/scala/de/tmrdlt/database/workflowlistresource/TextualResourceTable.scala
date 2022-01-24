package de.tmrdlt.database.workflowlistresource

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyDB.workflowListQuery
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListTable}
import de.tmrdlt.models.{WorkflowListResource, TextualResourceEntity}
import slick.lifted.{ForeignKeyQuery, Index, ProvenShape, Rep}
import slick.sql.SqlProfile.ColumnOption.NotNull

import java.time.LocalDateTime

case class TextualResource(id: Long,
                           workflowListId: Long,
                           label: String,
                           value: Option[String],
                           createdAt: LocalDateTime,
                           updatedAt: LocalDateTime) extends WorkflowListResource {

  def toTextualResourceEntity: TextualResourceEntity =
    TextualResourceEntity(label = label, value = value)
}

class TextualResourceTable(tag: Tag)
  extends BaseTableLong[TextualResource](tag, "textual_resource") {

  def workflowListId: Rep[Long] = column[Long]("workflow_list_id", NotNull)

  def label: Rep[String] = column[String]("label", NotNull)

  def value: Rep[Option[String]] = column[Option[String]]("value", NotNull)

  def createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at", NotNull)

  def updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at", NotNull)

  def * : ProvenShape[TextualResource] = (
    id,
    workflowListId,
    label,
    value,
    createdAt,
    updatedAt
  ) <> (TextualResource.tupled, TextualResource.unapply)

  def workflowListForeignKey: ForeignKeyQuery[WorkflowListTable, WorkflowList] =
    foreignKey("workflow_list_fk", workflowListId, workflowListQuery)(_.id, onDelete = ForeignKeyAction.Cascade)

  def workflowListIdLabelIndex: Index = index(
    "textual_resource_workflow_list_id_label_unique_constraint",
    (workflowListId, label),
    unique = true
  )

}