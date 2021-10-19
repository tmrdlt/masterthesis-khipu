package de.tmrdlt.components.workflowlist

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{ApiErrorJsonSupport, CreateWorkflowListEntity, WorkflowListJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class WorkflowListRoute(controller: WorkflowListController)
  extends ApiErrorJsonSupport
    with WorkflowListJsonSupport
    with SimpleNameLogger {

  val route: Route = {
    concat(
      get {
        parameters("userApiId".?) { userApiId =>
            onSuccess(controller.getWorkflowListEntities(userApiId)) { workflowListEntities =>
              complete(OK -> workflowListEntities)
            }
        }
      },
      post {
        entity(as[CreateWorkflowListEntity]) { createWorkFlowListEntity =>
          onComplete(controller.createWorkflowList(createWorkFlowListEntity)) {
            case Success(apiId) => complete(OK -> apiId)
            case Failure(exception) => complete(exception.toResponseMarshallable)
          }
        }
      }
    )

  }
}