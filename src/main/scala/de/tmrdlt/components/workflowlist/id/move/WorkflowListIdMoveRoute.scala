package de.tmrdlt.components.workflowlist.id.move

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{as, complete, entity, onComplete, put}
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{ApiErrorJsonSupport, MoveWorkflowListEntity, WorkflowListJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import java.util.UUID
import scala.util.{Failure, Success}

class WorkflowListIdMoveRoute(controller: WorkflowListIdMoveController)
  extends ApiErrorJsonSupport
    with WorkflowListJsonSupport
    with SimpleNameLogger {

  def route(workflowListUUID: UUID): Route = {
    put {
      entity(as[MoveWorkflowListEntity]) { moveWorkflowListEntity =>
        onComplete(controller.moveWorkflowList(workflowListUUID, moveWorkflowListEntity)) {
          case Success(_) => complete(OK)
          case Failure(exception) => complete(exception.toResponseMarshallable)
        }
      }
    }
  }
}