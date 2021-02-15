package de.tmrdlt.database.trello

import de.tmrdlt.database.BaseTableString
import de.tmrdlt.database.MyDB.trelloBoardQuery
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.{TrelloBoard, TrelloList}
import slick.lifted.{ForeignKeyQuery, ProvenShape, Rep, Tag}
import slick.sql.SqlProfile.ColumnOption.NotNull

class TrelloListTable(tag: Tag) extends BaseTableString[TrelloList](tag, "trello_list") {

  def name: Rep[String] = column[String]("name", NotNull)

  def closed: Rep[Boolean] = column[Boolean]("closed", NotNull)

  def pos: Rep[Long] = column[Long]("pos", NotNull)

  def idBoard: Rep[String] = column[String]("id_board", NotNull)

  def boardForeignKey: ForeignKeyQuery[TrelloBoardTable, TrelloBoard] =
    foreignKey("board_fk", idBoard, trelloBoardQuery)(_.id, onDelete = ForeignKeyAction.Cascade)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def * : ProvenShape[TrelloList] =
    (id, name, closed, pos, idBoard).mapTo[TrelloList]
}
