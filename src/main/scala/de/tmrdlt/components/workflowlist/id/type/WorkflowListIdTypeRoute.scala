package de.tmrdlt.components.workflowlist.id.`type`

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{as, complete, entity, onComplete, put}
import akka.http.scaladsl.server.Route
import de.tmrdlt.directives.AuthorizationDirective
import de.tmrdlt.models.{ApiErrorJsonSupport, ConvertWorkflowListEntity, WorkflowListJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class WorkflowListIdTypeRoute(controller: WorkflowListIdTypeController,
                              directive: AuthorizationDirective)
  extends ApiErrorJsonSupport
    with WorkflowListJsonSupport
    with SimpleNameLogger {

  def route(workflowListApiId: String): Route = {
    put {
      directive.authorizeUser { userApiId =>
        entity(as[ConvertWorkflowListEntity]) { entity =>
          onComplete(controller.convertWorkflowList(workflowListApiId, entity, userApiId)) {
            case Success(_) => complete(OK)
            case Failure(exception) => complete(exception.toResponseMarshallable)
          }
        }
      }
    }
  }
}