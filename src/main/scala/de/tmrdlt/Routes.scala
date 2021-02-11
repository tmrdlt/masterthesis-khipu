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
        path("fetchTrelloBoard")(components.fetchTrelloBoard.route),
        path("workflowList")(components.workflowList.route),
        path("workflowList" / JavaUUID ) { workflowListUUID => components.workflowListId.route(workflowListUUID) },
        path("workflowList" / JavaUUID / "convert") { workflowListUUID => components.workflowListIdConvert.route(workflowListUUID) },
        path("workflowList" / JavaUUID / "move") { workflowListUUID => components.workflowListIdMove.route(workflowListUUID) },
        path("workflowList" / JavaUUID / "reorder") { workflowListUUID => components.workflowListIdReorder.route(workflowListUUID) }
      )
    }
  }
}
