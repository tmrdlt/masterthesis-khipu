package de.tmrdlt.components.workflowlist

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{as, complete, entity, onComplete, post}
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{ApiErrorJsonSupport, CreateWorkflowListEntity, WorkflowListJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class WorkflowListRoute(controller: WorkflowListController)
  extends ApiErrorJsonSupport
    with WorkflowListJsonSupport
    with SimpleNameLogger {

  val route: Route =
    post {
      entity(as[CreateWorkflowListEntity]) { createWorkFlowListEntity =>
        onComplete(controller.createWorkflowList(createWorkFlowListEntity)) {
          case Success(_) => complete(OK)
          case Failure(exception) => complete(exception.toResponseMarshallable)
        }
      }
    }
}