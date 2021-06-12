package de.tmrdlt.components.workflowlist.id.resource.temporal

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{as, complete, entity, onComplete, post}
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{ApiErrorJsonSupport, TemporalResourceEntity, WorkflowListJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class WorkflowListIdTempResourceRoute(controller: WorkflowListIdTempResourceController)
  extends ApiErrorJsonSupport
    with WorkflowListJsonSupport
    with SimpleNameLogger {

  def route(workflowListApiId: String): Route = {
    post {
      entity(as[TemporalResourceEntity]) { temporalResourceEntity =>
        onComplete(controller.createOrUpdateTemporalResource(workflowListApiId, temporalResourceEntity)) {
          case Success(res) => complete(OK -> res.toString)
          case Failure(exception) =>
            log.error("error", exception)
            complete(exception.toResponseMarshallable)
        }
      }
    }
  }
}
