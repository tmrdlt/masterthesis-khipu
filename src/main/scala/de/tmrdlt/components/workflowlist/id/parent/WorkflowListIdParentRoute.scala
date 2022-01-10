package de.tmrdlt.components.workflowlist.id.parent

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{as, complete, entity, onComplete, put}
import akka.http.scaladsl.server.Route
import de.tmrdlt.directives.AuthorizationDirective
import de.tmrdlt.models.{ApiErrorJsonSupport, MoveWorkflowListEntity, WorkflowListJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class WorkflowListIdParentRoute(controller: WorkflowListIdParentController,
                                directive: AuthorizationDirective)
  extends ApiErrorJsonSupport
    with WorkflowListJsonSupport
    with SimpleNameLogger {

  def route(workflowListApiId: String): Route = {
    put {
      directive.authorizeUser { userApiId =>
        entity(as[MoveWorkflowListEntity]) { entity =>
          onComplete(controller.moveWorkflowList(workflowListApiId, entity, userApiId)) {
            case Success(res) => complete(OK -> res.toString)
            case Failure(exception) => complete(exception.toResponseMarshallable)
          }
        }
      }
    }
  }
}