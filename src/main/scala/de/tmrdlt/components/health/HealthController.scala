package de.tmrdlt.components.health

import akka.actor.ActorRef
import akka.pattern.ask
import de.tmrdlt.components.health.HealthActor.DoHealthCheck
import de.tmrdlt.utils.{ActorTimeout, SimpleNameLogger}

import scala.concurrent.Future

class HealthController(healthActor: ActorRef) extends ActorTimeout with SimpleNameLogger {

  def performHealthCheck(): Future[Boolean] = (healthActor ? DoHealthCheck).mapTo[Boolean]
}
