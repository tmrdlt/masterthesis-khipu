package de.tmrdlt.components.workflowlist.id.query

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{complete, get, onComplete}
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{ApiErrorJsonSupport, WorkflowListQueryJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class WorkflowListIdQueryRoute(controller: WorkflowListIdQueryController)
  extends ApiErrorJsonSupport
    with SimpleNameLogger with WorkflowListQueryJsonSupport {

  def route(workflowListApiId: String): Route = {
    get {
      onComplete(controller.performTemporalQuery(workflowListApiId)) {
        case Success(result) => complete(OK -> result)
        case Failure(exception) => complete(exception.toResponseMarshallable)
      }
    }
  }
}
