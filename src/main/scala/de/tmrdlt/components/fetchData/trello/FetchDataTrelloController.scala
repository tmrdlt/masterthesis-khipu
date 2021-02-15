package de.tmrdlt.components.fetchData.trello

import akka.actor.ActorRef
import de.tmrdlt.components.fetchData.trello.FetchDataTrelloActor.FetchDataTrello
import de.tmrdlt.connectors.TrelloApi
import de.tmrdlt.database.trello.TrelloDB
import de.tmrdlt.utils.SimpleNameLogger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FetchDataTrelloController(trelloApi: TrelloApi,
                                trelloDB: TrelloDB,
                                fetchDataTrelloActor: ActorRef)
  extends SimpleNameLogger {

  def fetchDataTrello(boardIds: Seq[String]): Future[Int] = {
    // Defining the futures before the for yield makes them run in parallel
    // val boardFuture = Future.sequence(boardIds.map(b => trelloApi.getBoard(b)))
    // val listsOfBoardFuture = Future.sequence(boardIds.map(b => trelloApi.getListOnABoard(b))).map(_.flatten)
    // val actionsOfBoardFuture = Future.sequence(boardIds.map(b => trelloApi.getActionsOfABoard(b))).map(_.flatten)

    for {
      boards <- Future.sequence(boardIds.map(b => trelloApi.getBoard(b)))
      listsOfBoard <- Future.sequence(boardIds.map(b => trelloApi.getListOnABoard(b))).map(_.flatten)
      actionsOfBoard <- Future.sequence(boardIds.map(b => trelloApi.getActionsOfABoard(b))).map(_.flatten)
      cardsOfBoard <- Future.sequence(listsOfBoard.map(l => trelloApi.getCardsInAList(l.id))).map(_.flatten)

      insertedBoards <- trelloDB.insertTrelloBoards(boards)
      insertedLists <- trelloDB.insertTrelloLists(listsOfBoard)
      insertedCards <- trelloDB.insertTrelloCards(cardsOfBoard)
      insertedActions <- trelloDB.insertTrelloActions(actionsOfBoard.map(_.toTrelloActionDBEntity))

    } yield {
      insertedActions
    }
  }
}
