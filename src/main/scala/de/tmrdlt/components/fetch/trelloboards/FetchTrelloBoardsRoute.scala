package de.tmrdlt.components.fetch.trelloboards

import akka.http.scaladsl.model.StatusCodes.{InternalServerError, OK}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{FetchTrelloBoardsEntity, TrelloJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class FetchTrelloBoardsRoute(controller: FetchTrelloBoardsController)
  extends SimpleNameLogger with TrelloJsonSupport {

  // TODO use Auth for all routes
  val route: Route =
    post {
      entity(as[FetchTrelloBoardsEntity]) { fetchTrelloBoardsEntity =>
        onComplete(controller.fetchTrelloBoards(fetchTrelloBoardsEntity.boardIds)) {
          case Success(res) => complete(OK, res)
          case Failure(e) =>
            log.error(e.getMessage)
            complete(InternalServerError)
        }
      }
    }
}
