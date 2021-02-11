package de.tmrdlt.components.workflowlist.id.reorder

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{as, complete, entity, onComplete, post}
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{ApiErrorJsonSupport, MoveWorkflowListEntity, ReorderWorkflowListEntity, WorkflowListJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import java.util.UUID
import scala.util.{Failure, Success}

class WorkflowListIdReorderRoute(controller: WorkflowListIdReorderController)
  extends ApiErrorJsonSupport
    with WorkflowListJsonSupport
    with SimpleNameLogger {

  def route(workflowListUuid: UUID): Route = {
    post {
      entity(as[ReorderWorkflowListEntity]) { reorderWorkflowListEntity =>
        onComplete(controller.reorderWorkflowList(workflowListUuid, reorderWorkflowListEntity)) {
          case Success(res) => complete(OK -> res.toString)
          case Failure(exception) => complete(exception.toResponseMarshallable)
        }
      }
    }
  }
}
