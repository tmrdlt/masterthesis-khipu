package de.tmrdlt.components.fetchData.github

import akka.http.scaladsl.model.StatusCodes.{InternalServerError, OK}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{FetchDataGitHubEntity, GitHubJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

import scala.util.{Failure, Success}

class FetchDataGitHubRoute(controller: FetchDataGitHubController)
  extends SimpleNameLogger with GitHubJsonSupport {

  val route: Route =
    post {
      entity(as[FetchDataGitHubEntity]) { fetchDataGitHubEntity =>
        onComplete(controller.fetchDataGitHub(fetchDataGitHubEntity.orgNames)) {
          case Success(res) => complete(OK, res)
          case Failure(e) =>
            log.error(e.getMessage)
            complete(InternalServerError)
        }
      }
    }
}