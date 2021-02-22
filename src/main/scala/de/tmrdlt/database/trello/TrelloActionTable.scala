package de.tmrdlt.database.trello

import de.tmrdlt.database.BaseTableString
import de.tmrdlt.database.MyPostgresProfile.api._
import slick.lifted.{ProvenShape, Rep, Tag}
import slick.sql.SqlProfile.ColumnOption.{NotNull, Nullable}

import java.time.LocalDateTime

case class TrelloActionDBEntity(id: String,
                                `type`: String,
                                idBoard: String,
                                idList: Option[String],
                                idCard: Option[String],
                                date: LocalDateTime)

class TrelloActionTable(tag: Tag) extends BaseTableString[TrelloActionDBEntity](tag, "trello_action") {

  def `type`: Rep[String] = column[String]("type", NotNull)

  def idBoard: Rep[String] = column[String]("id_board", NotNull)

  def idList: Rep[Option[String]] = column[Option[String]]("id_list", Nullable)

  def idCard: Rep[Option[String]] = column[Option[String]]("id_card", Nullable)

  def date: Rep[LocalDateTime] = column[LocalDateTime]("date", NotNull)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def * : ProvenShape[TrelloActionDBEntity] =
    (id, `type`, idBoard, idList, idCard, date).mapTo[TrelloActionDBEntity]
}