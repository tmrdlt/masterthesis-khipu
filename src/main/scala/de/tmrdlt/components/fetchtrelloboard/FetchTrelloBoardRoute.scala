package de.tmrdlt.components.fetchtrelloboard

import akka.http.scaladsl.model.StatusCodes.{InternalServerError, OK}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.TrelloJsonSupport
import de.tmrdlt.services.AuthorizeWorkflowTokenService.authorizeWorkflowToken
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class FetchTrelloBoardRoute(controller: FetchTrelloBoardController)
  extends SimpleNameLogger with TrelloJsonSupport {

  // TODO use Auth for all routes
  val route: Route =
    post {
      entity(as[String]) { boardId =>
        onComplete(controller.fetchData(boardId)) {
          case Success(res) => complete(OK, res)
          case Failure(e) =>
            log.error(e.getMessage)
            complete(InternalServerError)
        }
      }
    }
}
