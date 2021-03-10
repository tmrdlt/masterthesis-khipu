package de.tmrdlt.database.action

import de.tmrdlt.database.MyDB.{actionQuery, db}
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}

import scala.concurrent.Future

class ActionDB
  extends SimpleNameLogger
    with OptionExtensions {

  def insertActions(actions: Seq[Action]): Future[Seq[Action]] =
    db.run(actionQuery returning actionQuery ++= actions)

  def getActions: Future[Seq[Action]] =
    db.run(actionQuery.result)

}
