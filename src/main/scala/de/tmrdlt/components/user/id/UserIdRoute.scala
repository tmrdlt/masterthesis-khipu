package de.tmrdlt.components.user.id

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{complete, get, onComplete}
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{ApiErrorJsonSupport, UserJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class UserIdRoute(controller: UserIdController) extends
  ApiErrorJsonSupport
  with UserJsonSupport
  with SimpleNameLogger {

  def route(userApiId: String): Route = {
    get {
      onComplete(controller.getUser(userApiId)) {
        case Success(user) => complete(OK -> user)
        case Failure(exception) => complete(exception.toResponseMarshallable)
      }
    }
  }
}
