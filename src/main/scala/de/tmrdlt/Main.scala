package de.tmrdlt

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import de.tmrdlt.components.Components
import de.tmrdlt.utils.WorkflowConfig

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object Main extends App with WorkflowConfig {

  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = system.dispatcher


  val endPoints: Route = new Routes(new Components(system)).endPoints

  val host: String = config.getString("workflow.http.host")
  val port: Int = config.getInt("workflow.http.port")

  val bindingFuture: Future[ServerBinding] = Http().newServerAt(host, port).bind(endPoints)

  val log = Logging(system.eventStream, "workflow")
  bindingFuture onComplete {
    case Success(serverBinding) =>
      log.info(s"Workflow API bound to ${serverBinding.localAddress}")
    case Failure(ex) =>
      log.error(ex, "Failed to bind to {}:{}", host, port)
      system.terminate()
  }
}
