package de.tmrdlt.database.trello

import de.tmrdlt.database.MyDB._
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TrelloDB
  extends SimpleNameLogger
    with OptionExtensions {

  def insertTrelloActions(trelloActions: Seq[TrelloActionDBEntity]): Future[Int] =
    db.run(trelloActionQuery returning trelloActionQuery ++= trelloActions).map(_.length)

}
