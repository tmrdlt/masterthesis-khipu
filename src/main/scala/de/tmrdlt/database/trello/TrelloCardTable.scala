package de.tmrdlt.database.trello

import de.tmrdlt.database.BaseTableString
import de.tmrdlt.database.MyDB.{trelloBoardQuery, trelloListQuery}
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.{TrelloBoard, TrelloCard, TrelloList}
import slick.lifted.{ForeignKeyQuery, ProvenShape, Rep, Tag}
import slick.sql.SqlProfile.ColumnOption.NotNull

import java.time.LocalDateTime

class TrelloCardTable(tag: Tag) extends BaseTableString[TrelloCard](tag, "trello_card") {

  def name: Rep[String] = column[String]("name", NotNull)

  def desc: Rep[String] = column[String]("desc", NotNull)

  def pos: Rep[Long] = column[Long]("pos", NotNull)

  def closed: Rep[Boolean] = column[Boolean]("closed", NotNull)

  def idBoard: Rep[String] = column[String]("id_board", NotNull)

  def idList: Rep[String] = column[String]("id_list", NotNull)

  def dateLastActivity: Rep[LocalDateTime] = column[LocalDateTime]("date_last_activity", NotNull)

  def boardForeignKey: ForeignKeyQuery[TrelloBoardTable, TrelloBoard] =
    foreignKey("board_fk", idBoard, trelloBoardQuery)(_.id, onDelete = ForeignKeyAction.Cascade)

  def listForeignKey: ForeignKeyQuery[TrelloListTable, TrelloList] =
    foreignKey("list_fk", idList, trelloListQuery)(_.id, onDelete = ForeignKeyAction.Cascade)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def * : ProvenShape[TrelloCard] =
    (id, name, desc, pos, closed, idBoard, idList, dateLastActivity).mapTo[TrelloCard]
}