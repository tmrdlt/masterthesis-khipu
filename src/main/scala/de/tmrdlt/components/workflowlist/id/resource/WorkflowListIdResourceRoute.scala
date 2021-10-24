package de.tmrdlt.components.workflowlist.id.resource

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{as, complete, entity, onComplete, post}
import akka.http.scaladsl.server.Route
import de.tmrdlt.directives.AuthorizationDirective
import de.tmrdlt.models.{ApiErrorJsonSupport, WorkflowListJsonSupport, WorkflowListResourceEntity}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class WorkflowListIdResourceRoute(controller: WorkflowListIdResourceController,
                                  directive: AuthorizationDirective)
  extends ApiErrorJsonSupport
    with WorkflowListJsonSupport
    with SimpleNameLogger {

  def route(workflowListApiId: String): Route = {
    post {
      directive.authorizeUser { userApiId =>
        entity(as[WorkflowListResourceEntity]) { entity =>
          onComplete(controller.updateWorkflowListResource(workflowListApiId, entity, userApiId)) {
            case Success(res) => complete(OK -> res.toString)
            case Failure(exception) => log.error("error", exception)
              complete(exception.toResponseMarshallable)
          }
        }
      }
    }
  }
}