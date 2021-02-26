package de.tmrdlt.database.moveaction

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyDB.actionQuery
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.database.action.{Action, ActionTable}
import slick.lifted.{ForeignKeyQuery, ProvenShape, Rep, Tag}
import slick.sql.SqlProfile.ColumnOption.NotNull

case class MoveAction(id: Long,
                      actionId: Long,
                      oldListApiId: String, // TODO make Long and foreign key
                      newListApiId: String) // TODO make Long and foreign key)

class MoveActionTable(tag: Tag) extends BaseTableLong[MoveAction](tag, "move_action") {

  def actionId: Rep[Long] = column[Long]("action_id", NotNull)

  def oldListApiId: Rep[String] = column[String]("old_list_api_id", NotNull)

  def newListApiId: Rep[String] = column[String]("new_list_api_id", NotNull)

  def actionForeignKey: ForeignKeyQuery[ActionTable, Action] =
    foreignKey("action_fk", actionId, actionQuery)(_.id, onDelete = ForeignKeyAction.Cascade)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def * : ProvenShape[MoveAction] =
    (id, actionId, oldListApiId, newListApiId).mapTo[MoveAction]
}