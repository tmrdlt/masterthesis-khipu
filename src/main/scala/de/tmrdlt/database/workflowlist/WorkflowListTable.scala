package de.tmrdlt.database.workflowlist

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyPostgresProfile.api._
import slick.lifted.ProvenShape
import slick.sql.SqlProfile.ColumnOption.{NotNull, Nullable}

import java.util.UUID

/**
 * Database representation of a workflowList.
 *
 * @param id          Database id
 * @param uuid        UUID (generated upon insert) to be used as a unique identifier
 *                    when the real db id is not desired.
 * @param title       TBD
 * @param description TBD
 */
case class WorkflowList(id: Long,
                        uuid: UUID,
                        title: String,
                        description: Option[String])

class WorkflowListTable(tag: Tag)
  extends BaseTableLong[WorkflowList](tag, "workflow_list") {

  def uuid: Rep[UUID] = column[UUID]("uuid", NotNull)

  def title: Rep[String] = column[String]("title", NotNull)

  def description: Rep[Option[String]] = column[Option[String]]("description", Nullable)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def * : ProvenShape[WorkflowList] =
    (id, uuid, title, description).mapTo[WorkflowList]
}
