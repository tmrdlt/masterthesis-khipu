package de.tmrdlt

import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Directives.{complete, extractUri, path, _}
import akka.http.scaladsl.server._
import de.tmrdlt.components.Components
import de.tmrdlt.utils.{PreflightUtil, SimpleNameLogger}


class Routes(components: Components)
  extends SimpleNameLogger with PreflightUtil {

  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: Exception => extractUri { uri =>
      log.error(s"Route failed $uri ${e.getMessage}", e)
      complete(InternalServerError, e.getMessage)
    }
  }

  def endPoints: Route = handleExceptions(exceptionHandler) {
    addAccessControlHeaders {
      concat(
        preflightRoute,
        path("health")(components.health.route),
        path("fetchdata" / "trello")(components.fetchDataTrello.route),
        path("fetchdata" / "github")(components.fetchDataGitHub.route),
        path("workflowlist")(components.workflowList.route),
        path("workflowlist" / Segment / "convert") { workflowListApiId => components.workflowListIdConvert.route(workflowListApiId) },
        path("workflowlist" / Segment / "move") { workflowListApiId => components.workflowListIdMove.route(workflowListApiId) },
        path("workflowlist" / Segment / "reorder") { workflowListApiId => components.workflowListIdReorder.route(workflowListApiId) },
        path("workflowlist" / Segment / "tempconstraint") { workflowListApiId => components.workflowListIdTempConstraint.route(workflowListApiId) },
        path("workflowlist" / Segment) { workflowListApiId => components.workflowListId.route(workflowListApiId) }
      )
    }
  }
}
