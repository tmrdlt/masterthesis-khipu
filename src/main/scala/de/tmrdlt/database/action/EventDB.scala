package de.tmrdlt.database.action

import de.tmrdlt.database.MyDB.{eventQuery, db}
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}

import scala.concurrent.Future

class EventDB
  extends SimpleNameLogger
    with OptionExtensions {

  def insertEvents(events: Seq[Event]): Future[Seq[Event]] =
    db.run(eventQuery returning eventQuery ++= events)

  def getEvents: Future[Seq[Event]] =
    db.run(eventQuery.result)

}
