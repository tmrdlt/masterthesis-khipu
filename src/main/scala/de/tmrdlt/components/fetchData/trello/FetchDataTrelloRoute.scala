package de.tmrdlt.components.fetchData.trello

import akka.http.scaladsl.model.StatusCodes.{InternalServerError, OK}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{FetchDataTrelloEntity, TrelloJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class FetchDataTrelloRoute(controller: FetchDataTrelloController)
  extends SimpleNameLogger with TrelloJsonSupport {

  // TODO use Auth for all routes
  val route: Route =
    post {
      entity(as[FetchDataTrelloEntity]) { fetchDataTrelloEntity =>
        onComplete(controller.fetchDataTrello(fetchDataTrelloEntity.boardIds)) {
          case Success(res) => complete(OK, res.toString)
          case Failure(e) =>
            log.error(e.getMessage)
            complete(InternalServerError)
        }
      }
    }
}
