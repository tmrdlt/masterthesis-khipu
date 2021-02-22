package de.tmrdlt.connectors

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.{Accept, Authorization, BasicHttpCredentials}
import akka.http.scaladsl.model.{HttpHeader, HttpMethods, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.{Http, HttpExt}
import de.tmrdlt.models.{GitHubCard, GitHubColumn, GitHubEvent, GitHubIssue, GitHubJsonSupport, GitHubProject}
import de.tmrdlt.utils.{SimpleNameLogger, WorkflowConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GitHubApi(implicit system: ActorSystem) extends SimpleNameLogger with WorkflowConfig with GitHubJsonSupport {

  val gitHubPersonalAccessToken = "ddfe1ca4a8bb8b96811f5a042dd602924842de9f"
  val gitHubUsername = "timunkulus"

  val projectsAcceptHeaderValue = "application/vnd.github.inertia-preview+json"
  val baseUrl = "https://api.github.com"

  val paginationElementsPerPage = 100

  val acceptHeader: HttpHeader = Accept.parseFromValueString(projectsAcceptHeaderValue) match {
    case Right(header) => header
    case Left(_) => throw new Exception("Error parsing accept header")
  }
  val authHeader: HttpHeader = Authorization(BasicHttpCredentials(gitHubUsername, gitHubPersonalAccessToken))

  val http: HttpExt = Http()

  def getProjectsOfOrganisation(orgName: String): Future[Seq[GitHubProject]] = {
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = s"${baseUrl}/orgs/${orgName}/projects",
      headers = List(acceptHeader)
    )
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[Seq[GitHubProject]]
    } yield {
      log.info("Got GitHub projects")
      res
    }
  }

  def getProjectsForRepository(repoOwnerString: String): Future[Seq[GitHubProject]] = {
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = s"${baseUrl}/repos/${repoOwnerString}/projects",
      headers = List(acceptHeader, authHeader)
    )
    log.info(s"URL: ${request.uri}")
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[Seq[GitHubProject]]
    } yield {
      log.info("Got GitHub projects")
      res
    }
  }

  def getColumnsOfProject(columns_url: String): Future[Seq[GitHubColumn]] = {
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = columns_url,
      headers = List(acceptHeader, authHeader)
    )
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[Seq[GitHubColumn]]
    } yield {
      log.info("Got GitHub columns")
      res
    }
  }

  def getCardsOfColumn(cards_url: String): Future[Seq[GitHubCard]] = {
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = cards_url,
      headers = List(acceptHeader, authHeader)
    )
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[Seq[GitHubCard]]
    } yield {
      log.info("Got GitHub cards")
      res
    }
  }

  def getContentOfNote(content_url: String): Future[GitHubIssue] = {
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = content_url,
      headers = List(acceptHeader, authHeader)
    )
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[GitHubIssue]
    } yield {
      log.info("Got GitHub issue")
      res
    }
  }

  def getEventsForIssue(eventsUrl: String, page: Int): Future[Seq[GitHubEvent]] = {
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = s"${eventsUrl}?per_page=${paginationElementsPerPage}&page=${page}",
      headers = List(acceptHeader, authHeader)
    )
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[Seq[GitHubEvent]]
    } yield {
      log.info("Got GitHub events")
      res
    }
  }
}
