package de.tmrdlt.components.workflowlist.id

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{as, complete, concat, delete, entity, onComplete, patch}
import akka.http.scaladsl.server.Route
import de.tmrdlt.directives.AuthorizationDirective
import de.tmrdlt.models.{ApiErrorJsonSupport, UpdateWorkflowListEntity, WorkflowListJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class WorkflowListIdRoute(controller: WorkflowListIdController,
                          directive: AuthorizationDirective)
  extends ApiErrorJsonSupport
    with WorkflowListJsonSupport
    with SimpleNameLogger {

  def route(workflowListApiId: String): Route = {
    concat(
      patch {
        directive.authorizeUser { userApiId =>
          entity(as[UpdateWorkflowListEntity]) { entity =>
            onComplete(controller.updateWorkflowList(workflowListApiId, entity, userApiId)) {
              case Success(_) => complete(OK)
              case Failure(exception) => complete(exception.toResponseMarshallable)
            }
          }
        }
      },
      delete {
        directive.authorizeUser { userApiId =>
          onComplete(controller.deleteWorkflowList(workflowListApiId, userApiId)) {
            case Success(_) => complete(OK)
            case Failure(exception) => complete(exception.toResponseMarshallable)
          }
        }
      }
    )
  }
}