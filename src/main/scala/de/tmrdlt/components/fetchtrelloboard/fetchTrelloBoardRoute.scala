package de.tmrdlt.components.fetchtrelloboard

import akka.http.scaladsl.model.StatusCodes.{InternalServerError, OK}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.tmrdlt.services.AuthorizeWorkflowTokenService.authorizeWorkflowToken
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class fetchTrelloBoardRoute(controller: fetchTrelloBoardController)
  extends SimpleNameLogger {

  val route: Route =
    post {
      authorizeWorkflowToken {
        entity(as[String]) { boardId =>
          onComplete(controller.fetchData(boardId)) {
            case Success(_) => complete(OK, "fetched board")
            case Failure(e) =>
              log.error(e.getMessage)
              complete(InternalServerError)
          }
        }
      }
    }
}
