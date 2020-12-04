package de.tmrdlt.services

import akka.http.scaladsl.model.StatusCodes.Unauthorized
import akka.http.scaladsl.server.Directives.{complete, headerValueByName}
import akka.http.scaladsl.server.Route
import de.tmrdlt.utils.WorkflowConfig

object AuthorizeWorkflowTokenService extends WorkflowConfig {

  val workflowToken: String = config.getString("workflow.token")

  def authorizeWorkflowToken(route: Route): Route =
    headerValueByName("Authorization") {
      _ == workflowToken match {
        case false => complete((Unauthorized, "No correct Authorization-Header set"))
        case true => route
      }
    }

}
