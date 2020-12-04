package de.tmrdlt.components.fetchtrelloboard

import de.tmrdlt.database.services.BoardDB
import de.tmrdlt.utils.SimpleNameLogger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class fetchTrelloBoardController(boardDB: BoardDB)
  extends SimpleNameLogger {

  def fetchData(boardId: String): Future[Boolean] = {
    for {
      result <- Future.successful(true)
    } yield result
  }
}
