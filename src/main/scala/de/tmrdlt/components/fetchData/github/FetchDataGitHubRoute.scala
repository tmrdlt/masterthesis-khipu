package de.tmrdlt.components.fetchData.github

import akka.http.scaladsl.model.StatusCodes.Accepted
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{FetchDataGitHubEntity, GitHubJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

class FetchDataGitHubRoute(controller: FetchDataGitHubController)
  extends SimpleNameLogger with GitHubJsonSupport {

  val route: Route =
    post {
      entity(as[FetchDataGitHubEntity]) { fetchDataGitHubEntity =>
        controller.fetchDataGitHub(fetchDataGitHubEntity)
        complete(Accepted)
      }
    }
}