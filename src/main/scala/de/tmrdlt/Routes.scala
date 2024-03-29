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
        path("fetchdata" / "github")(components.fetchDataGitHub.route),
        path("fetchdata" / "trello")(components.fetchDataTrello.route),
        path("workflowlist")(components.workflowList.route),
        path("workflowlist" / Segment) { workflowListApiId => components.workflowListId.route(workflowListApiId) },
        path("workflowlist" / Segment / "resource") { workflowListApiId => components.workflowListIdResource.route(workflowListApiId) },
        path("workflowlist" / Segment / "type") { workflowListApiId => components.workflowListIdType.route(workflowListApiId) },
        path("workflowlist" / Segment / "position") { workflowListApiId => components.workflowListIdPosition.route(workflowListApiId) },
        path("workflowlist" / Segment / "parent") { workflowListApiId => components.workflowListIdParent.route(workflowListApiId) },
        path("workflowlist" / Segment / "scheduling") { workflowListApiId => components.workflowListIdScheduling.route(workflowListApiId) },
        path("user")(components.user.route),
        path("user" / Segment) { userApiId => components.userId.route(userApiId) }
      )
    }
  }
}
