package de.tmrdlt.database.trello

import de.tmrdlt.database.MyDB._
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.{TrelloBoard, TrelloCard, TrelloList}
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TrelloDB
  extends SimpleNameLogger
    with OptionExtensions {

  def insertTrelloBoards(trelloBoards: Seq[TrelloBoard]): Future[Int] =
    db.run(trelloBoardQuery returning trelloBoardQuery ++= trelloBoards).map(_.length)

  def insertTrelloLists(trelloLists: Seq[TrelloList]): Future[Int] =
    db.run(trelloListQuery returning trelloListQuery ++= trelloLists).map(_.length)

  def insertTrelloCards(trelloCards: Seq[TrelloCard]): Future[Int] =
    db.run(trelloCardQuery returning trelloCardQuery ++= trelloCards).map(_.length)

  def insertTrelloActions(trelloActions: Seq[TrelloActionDBEntity]): Future[Int] =
    db.run(trelloActionQuery returning trelloActionQuery ++= trelloActions).map(_.length)

}
