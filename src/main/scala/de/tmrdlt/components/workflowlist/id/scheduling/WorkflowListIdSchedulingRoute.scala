package de.tmrdlt.components.workflowlist.id.scheduling

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{complete, get, onComplete}
import akka.http.scaladsl.server.Route
import de.tmrdlt.directives.AuthorizationDirective
import de.tmrdlt.models.{ApiErrorJsonSupport, WorkflowListQueryJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class WorkflowListIdSchedulingRoute(controller: WorkflowListIdQueryController,
                                    directive: AuthorizationDirective)
  extends ApiErrorJsonSupport
    with SimpleNameLogger with WorkflowListQueryJsonSupport {

  def route(workflowListApiId: String): Route = {
    get {
      directive.authorizeUser { userApiId =>
        onComplete(controller.performTemporalQuery(workflowListApiId, userApiId)) {
          case Success(result) => complete(OK -> result)
          case Failure(exception) => complete(exception.toResponseMarshallable)
        }
      }
    }
  }
}
