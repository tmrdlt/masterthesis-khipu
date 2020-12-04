package de.tmrdlt.database.tables

import de.tmrdlt.database.WorkflowPostgresProfile.api._
import de.tmrdlt.models._
import slick.lifted.ProvenShape
import slick.sql.SqlProfile.ColumnOption.NotNull

import java.util.UUID


class BoardTable(tag: Tag)
  extends BaseTableLong[Board](tag, "attachment") {

  def uuid: Rep[UUID] = column[UUID]("mail_id", NotNull)

  def name: Rep[String] = column[String]("attachment_key", NotNull)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def * : ProvenShape[Board] =
    (uuid, name).mapTo[Board]
}
