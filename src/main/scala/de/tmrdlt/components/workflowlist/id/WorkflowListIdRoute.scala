package de.tmrdlt.components.workflowlist.id

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{as, complete, concat, delete, entity, onComplete, put}
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{ApiErrorJsonSupport, UpdateWorkflowListEntity, WorkflowListJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import java.util.UUID
import scala.util.{Failure, Success}

class WorkflowListIdRoute(controller: WorkflowListIdController)
  extends ApiErrorJsonSupport
    with WorkflowListJsonSupport
    with SimpleNameLogger {

  def route(workflowListApiId: String): Route = {
    concat(
      put {
        entity(as[UpdateWorkflowListEntity]) { updateWorkflowListEntity =>
          onComplete(controller.updateWorkflowList(workflowListApiId, updateWorkflowListEntity)) {
            case Success(_) => complete(OK)
            case Failure(exception) => complete(exception.toResponseMarshallable)
          }
        }
      },
      delete {
        onComplete(controller.deleteWorkflowList(workflowListApiId)) {
          case Success(_) => complete(OK)
          case Failure(exception) => complete(exception.toResponseMarshallable)
        }
      }
    )
  }
}