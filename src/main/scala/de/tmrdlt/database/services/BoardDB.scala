package de.tmrdlt.database.services

import de.tmrdlt.utils.SimpleNameLogger
import de.tmrdlt.utils.OptionExtensions

import de.tmrdlt.database.WorkflowDB._
import de.tmrdlt.database.WorkflowPostgresProfile.api._
import de.tmrdlt.models._

class BoardDB
  extends SimpleNameLogger
    with OptionExtensions {

  private def insertBoardQuery(board: Board) =
    (boardQuery returning boardQuery) += board

}
