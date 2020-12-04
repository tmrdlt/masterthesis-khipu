package de.tmrdlt

import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Directives.{complete, extractUri, path, _}
import akka.http.scaladsl.server._
import de.tmrdlt.components.Components
import de.tmrdlt.utils.SimpleNameLogger


class Routes(components: Components)
  extends SimpleNameLogger {

  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: Exception => extractUri { uri =>
      log.error(s"Route failed $uri ${e.getMessage}", e)
      complete(InternalServerError, e.getMessage)
    }
  }

  def endPoints: Route = handleExceptions(exceptionHandler) {
    concat(
      path("health")(components.health.route),
      path("fetchTrelloBoard")(components.fetchTrelloBoard.route)
    )
  }
}
