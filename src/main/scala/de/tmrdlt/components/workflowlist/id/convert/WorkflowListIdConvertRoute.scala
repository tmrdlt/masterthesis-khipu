package de.tmrdlt.components.workflowlist.id.convert

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{as, complete, entity, onComplete, put}
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{ApiErrorJsonSupport, ConvertWorkflowListEntity, WorkflowListJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class WorkflowListIdConvertRoute(controller: WorkflowListIdConvertController)
  extends ApiErrorJsonSupport
    with WorkflowListJsonSupport
    with SimpleNameLogger {

  def route(workflowListId: Long): Route =
    put {
      entity(as[ConvertWorkflowListEntity]) { convertWorkflowListEntity =>
        onComplete(controller.convertWorkflowList(workflowListId, convertWorkflowListEntity.convertTo)) {
          case Success(_) => complete(OK)
          case Failure(exception) => complete(exception.toResponseMarshallable)
        }
      }
    }
}