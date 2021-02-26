package de.tmrdlt.database.action

import de.tmrdlt.database.MyDB.{actionQuery, db, moveActionQuery}
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.database.moveaction.MoveAction
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}

import scala.concurrent.Future

class ActionDB
  extends SimpleNameLogger
    with OptionExtensions {

  def insertActions(actions: Seq[Action]): Future[Seq[Action]] =
    db.run(actionQuery returning actionQuery ++= actions)

  def insertMoveActions(moveActions: Seq[MoveAction]): Future[Seq[MoveAction]] =
    db.run(moveActionQuery returning moveActionQuery ++= moveActions)

}
