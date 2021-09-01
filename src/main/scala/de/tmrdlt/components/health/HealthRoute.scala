package de.tmrdlt.components.health

import akka.http.scaladsl.marshalling.ToResponseMarshallable.apply
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.tmrdlt.utils.{ActorTimeout, SimpleNameLogger}

class HealthRoute(controller: HealthController)
  extends ActorTimeout
    with SimpleNameLogger {

  val route: Route =
    get {
      onSuccess(controller.solve()) { _ =>
        complete("Server is Healthy!")
      }
    }
}

