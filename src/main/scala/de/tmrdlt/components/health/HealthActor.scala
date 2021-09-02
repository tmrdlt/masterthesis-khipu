package de.tmrdlt.components.health

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe
import de.tmrdlt.components.health.HealthActor.DoHealthCheck

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class HealthActor() extends Actor with ActorLogging {

  override def receive: PartialFunction[Any, Unit] = {

    case DoHealthCheck =>
      pipe(
        Future.successful(true)
      ) to sender()
  }
}


object HealthActor {

  def props(): Props =
    Props(new HealthActor())

  val name = "HealthActor"

  case object DoHealthCheck

}
