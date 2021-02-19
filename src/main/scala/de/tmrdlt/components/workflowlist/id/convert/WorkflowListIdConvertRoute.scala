package de.tmrdlt.components.workflowlist.id.convert

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{as, complete, entity, onComplete, post}
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{ApiErrorJsonSupport, ConvertWorkflowListEntity, WorkflowListJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import java.util.UUID
import scala.util.{Failure, Success}

class WorkflowListIdConvertRoute(controller: WorkflowListIdConvertController)
  extends ApiErrorJsonSupport
    with WorkflowListJsonSupport
    with SimpleNameLogger {

  def route(workflowListApiId: String): Route = {
    post {
      entity(as[ConvertWorkflowListEntity]) { convertWorkflowListEntity =>
        onComplete(controller.convertWorkflowList(workflowListApiId, convertWorkflowListEntity)) {
          case Success(_) => complete(OK)
          case Failure(exception) => complete(exception.toResponseMarshallable)
        }
      }
    }
  }
}