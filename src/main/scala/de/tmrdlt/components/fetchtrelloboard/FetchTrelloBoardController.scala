package de.tmrdlt.components.fetchtrelloboard

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.utils.SimpleNameLogger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FetchTrelloBoardController(workflowListDB: WorkflowListDB)
  extends SimpleNameLogger {

  def fetchData(boardId: String): Future[Boolean] = {
    for {
      result <- Future.successful(true)
    } yield result
  }
}
