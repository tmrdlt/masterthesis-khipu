package de.tmrdlt.components.workflowlist.id.tempconstraint

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{as, complete, entity, onComplete, post}
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{ApiErrorJsonSupport, TemporalConstraintEntity, WorkflowListJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class WorkflowListIdTempConstraintRoute(controller: WorkflowListIdTempConstraintController)
  extends ApiErrorJsonSupport
    with WorkflowListJsonSupport
    with SimpleNameLogger {

  def route(workflowListApiId: String): Route = {
    post {
      entity(as[TemporalConstraintEntity]) { temporalConstraintEntity =>
        onComplete(controller.createOrUpdateTemporalConstraint(workflowListApiId, temporalConstraintEntity)) {
          case Success(res) => complete(OK -> res.toString)
          case Failure(exception) =>
            log.error("error", exception)
            complete(exception.toResponseMarshallable)
        }
      }
    }
  }
}
