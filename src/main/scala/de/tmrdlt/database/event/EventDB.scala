package de.tmrdlt.database.event

import de.tmrdlt.database.MyDB.{db, eventQuery}
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}

import scala.concurrent.Future

class EventDB
  extends SimpleNameLogger
    with OptionExtensions {

  def insertEvent(event: Event): Future[Event] =
    db.run(eventQuery returning eventQuery += event)

  def insertEvents(events: Seq[Event]): Future[Seq[Event]] =
    db.run(eventQuery returning eventQuery ++= events)

  def getEvents: Future[Seq[Event]] =
    db.run(eventQuery.result)

}
