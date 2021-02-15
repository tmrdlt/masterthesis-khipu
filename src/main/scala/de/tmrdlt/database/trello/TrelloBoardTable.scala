package de.tmrdlt.database.trello

import de.tmrdlt.database.BaseTableString
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.TrelloBoard
import slick.lifted.{ProvenShape, Rep, Tag}
import slick.sql.SqlProfile.ColumnOption.NotNull


class TrelloBoardTable(tag: Tag) extends BaseTableString[TrelloBoard](tag, "trello_board") {

  def name: Rep[String] = column[String]("name", NotNull)
  def desc: Rep[String] = column[String]("desc", NotNull)
  def closed: Rep[Boolean] = column[Boolean]("closed", NotNull)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def * : ProvenShape[TrelloBoard] =
    (id, name, desc, closed).mapTo[TrelloBoard]
}

