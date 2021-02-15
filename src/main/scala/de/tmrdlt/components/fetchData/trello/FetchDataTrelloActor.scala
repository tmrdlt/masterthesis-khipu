package de.tmrdlt.components.fetchData.trello

import akka.actor.{Actor, ActorLogging, Props}
import de.tmrdlt.components.fetchData.trello.FetchDataTrelloActor.FetchDataTrello
import de.tmrdlt.connectors.TrelloApi
import de.tmrdlt.database.trello.TrelloDB

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global


class FetchDataTrelloActor(trelloApi: TrelloApi,
                           trelloDB: TrelloDB) extends Actor with ActorLogging {

  override def receive: PartialFunction[Any, Unit] = {

    case FetchDataTrello(boardIds) => {
      // Defining the futures before the for yield makes them run in parallel
      val boardFuture = Future.sequence(boardIds.map(b => trelloApi.getBoard(b)))
      val listsOfBoardFuture = Future.sequence(boardIds.map(b => trelloApi.getListOnABoard(b))).map(_.flatten)
      val actionsOfBoardFuture = Future.sequence(boardIds.map(b => trelloApi.getActionsOfABoard(b))).map(_.flatten)

      for {
        boards <- boardFuture
        listsOfBoard <- listsOfBoardFuture
        actionsOfBoard <- actionsOfBoardFuture
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
}


object FetchDataTrelloActor {

  def props(trelloApi: TrelloApi,
            trelloDB: TrelloDB): Props =
    Props(new FetchDataTrelloActor(trelloApi, trelloDB))

  val name = "FetchDataTrelloActor"


  case class FetchDataTrello(boardIds: Seq[String])

}