package de.tmrdlt.components.fetchtrelloboard

import de.tmrdlt.connectors.TrelloApi
import de.tmrdlt.models.TrelloBoard
import de.tmrdlt.utils.SimpleNameLogger

import scala.concurrent.Future

class FetchTrelloBoardController(trelloApi: TrelloApi)
  extends SimpleNameLogger {

  def fetchData(boardId: String): Future[TrelloBoard] = {
    trelloApi.getTrelloBoard(boardId)
  }
}
