package de.tmrdlt.components.user

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{as, complete, concat, entity, get, onComplete, onSuccess, post}
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{ApiErrorJsonSupport, CreateUserEntity, UserJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class UserRoute(controller: UserController)
  extends ApiErrorJsonSupport
    with UserJsonSupport
    with SimpleNameLogger {

  val route: Route = {
    concat(
      get {
        onSuccess(controller.getUsers) { users =>
          complete(OK -> users)
        }
      },
      post {
        entity(as[CreateUserEntity]) { createUserEntity =>
          onComplete(controller.createUser(createUserEntity)) {
            case Success(_) => complete(OK)
            case Failure(exception) => complete(exception.toResponseMarshallable)
          }
        }
      }
    )
  }

}
