package de.tmrdlt.components.workflowlist.id.resource.generic

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{as, complete, concat, delete, entity, onComplete, post}
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{ApiErrorJsonSupport, GenericResourceEntity, WorkflowListJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class WorkflowListIdGenericResourceRoute(controller: WorkflowListIdGenericResourceController)
  extends ApiErrorJsonSupport
    with WorkflowListJsonSupport
    with SimpleNameLogger {

  def route(workflowListApiId: String): Route = {
    concat(
      post {
        entity(as[Seq[GenericResourceEntity]]) { genericResourceEntities =>
          onComplete(controller.updateGenericResources(workflowListApiId, genericResourceEntities)) {
            case Success(res) => complete(OK -> res.toString)
            case Failure(exception) =>
              log.error("error", exception)
              complete(exception.toResponseMarshallable)
          }
        }
      },
      delete {
        entity(as[GenericResourceEntity]) { genericResourceEntity =>
          onComplete(controller.deleteGenericResource(workflowListApiId, genericResourceEntity)) {
            case Success(res) => complete(OK -> res.toString)
            case Failure(exception) =>
              log.error("error", exception)
              complete(exception.toResponseMarshallable)
          }
        }
      }

    )
  }
}