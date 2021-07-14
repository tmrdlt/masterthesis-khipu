package de.tmrdlt.components.workflowlist.id.resource

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{as, complete, entity, onComplete, post}
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{ApiErrorJsonSupport, WorkflowListJsonSupport, WorkflowListResourceEntity}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class WorkflowListIdResourceRoute(controller: WorkflowListIdResourceController)
  extends ApiErrorJsonSupport
    with WorkflowListJsonSupport
    with SimpleNameLogger {

  def route(workflowListApiId: String): Route = {
    post {
      entity(as[WorkflowListResourceEntity]) { resourceEntity =>
        onComplete(controller.updateWorkflowListResource(workflowListApiId, resourceEntity)) {
          case Success(res) => complete(OK -> res.toString)
          case Failure(exception) => log.error("error", exception)
            complete(exception.toResponseMarshallable)
        }
      }
    }
  }
}