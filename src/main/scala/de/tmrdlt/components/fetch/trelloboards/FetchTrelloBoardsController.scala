package de.tmrdlt.components.fetch.trelloboards

import de.tmrdlt.connectors.TrelloApi
import de.tmrdlt.models.TrelloAction
import de.tmrdlt.utils.SimpleNameLogger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FetchTrelloBoardsController(trelloApi: TrelloApi)
  extends SimpleNameLogger {

  def fetchTrelloBoards(boardIds: Seq[String]): Future[Seq[TrelloAction]] = {
    // Defining the futures before the for yield makes them run in parallel
    val boardFuture = Future.sequence(boardIds.map(b => trelloApi.getBoard(b)))
    val listsOfBoardFuture = Future.sequence(boardIds.map(b => trelloApi.getListOnABoard(b)))
    val actionsOfBoardFuture = Future.sequence(boardIds.map(b => trelloApi.getActionsOfABoard(b)))

    for {
      boards <- boardFuture
      listsOfBoard <- listsOfBoardFuture
      actionsOfBoard <- actionsOfBoardFuture
      cardsOfBoard <- Future.sequence(listsOfBoard.flatten.map(l => trelloApi.getCardsInAList(l.id))).map(_.flatten)
    } yield {
      actionsOfBoard.flatten
    }
  }
}
