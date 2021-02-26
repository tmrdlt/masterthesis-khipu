package de.tmrdlt.connectors

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials}
import akka.http.scaladsl.model.{HttpHeader, HttpMethods}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.{Http, HttpExt}
import de.tmrdlt.models._
import de.tmrdlt.utils.{HttpUtil, SimpleNameLogger, WorkflowConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GitHubApi(implicit system: ActorSystem) extends SimpleNameLogger with WorkflowConfig with GitHubJsonSupport {
  val http: HttpExt = Http()

  // Base URL
  // https://docs.github.com/en/rest/overview/resources-in-the-rest-api#schema
  val baseUrl = "https://api.github.com"

  // Authentication
  // https://docs.github.com/en/rest/overview/resources-in-the-rest-api#authentication
  val gitHubPersonalAccessToken = "ddfe1ca4a8bb8b96811f5a042dd602924842de9f"
  val gitHubUsername = "timunkulus"
  val authHeader: HttpHeader = Authorization(BasicHttpCredentials(gitHubUsername, gitHubPersonalAccessToken))

  // Required Headers

  // https://docs.github.com/en/rest/reference/projects
  // Preview notice:
  // "The Projects API is currently available for developers to preview. During the preview period, the API may change
  // without advance notice. Please see the blog post for full details.
  // To access the API during the preview period, you most provide a custom media type in the Accept header:
  // application/vnd.github.inertia-preview+json"
  val projectsApiAcceptHeader: HttpHeader =
  HttpUtil.parseAcceptHeader("application/vnd.github.inertia-preview+json")

  // https://docs.github.com/en/rest/reference/issues#list-issue-events
  // Preview notice:
  // "Project card details are now shown in REST API v3 responses for project-related issue and timeline events. This
  // feature is now available for developers to preview. For details, see the blog post.
  // To receive the project_card attribute, project boards must be enabled for a repository, and you must provide a
  // custom media type in the Accept header:
  //application/vnd.github.starfox-preview+json"
  val eventsApiAcceptHeader: HttpHeader =
  HttpUtil.parseAcceptHeader("application/vnd.github.starfox-preview+json")

  // Pagination
  // https://docs.github.com/en/rest/overview/resources-in-the-rest-api#pagination
  val paginationElementsPerPage = 100

  def gitHubPaginationParams(page: Int): Seq[(String, String)] =
    Seq(("per_page", s"$paginationElementsPerPage"), ("page", s"$page"))


  def getProjectsOfOrganisation(orgName: String): Future[Seq[GitHubProject]] = {
    val request = HttpUtil.request(
      method = HttpMethods.GET,
      headers = List(projectsApiAcceptHeader),
      path = s"${baseUrl}/orgs/${orgName}/projects",
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
    val request = HttpUtil.request(
      method = HttpMethods.GET,
      headers = List(projectsApiAcceptHeader, authHeader),
      path = s"${baseUrl}/repos/${repoOwnerString}/projects",
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

  def getColumnsOfProject(columnsUrl: String): Future[Seq[GitHubColumn]] = {
    val request = HttpUtil.request(
      method = HttpMethods.GET,
      headers = List(projectsApiAcceptHeader, authHeader),
      path = columnsUrl,
    )
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[Seq[GitHubColumn]]
    } yield {
      log.info("Got GitHub columns")
      res
    }
  }

  // ToDo add pagination
  def getCardsOfColumn(cardsUrl: String): Future[Seq[GitHubCard]] = {
    val request = HttpUtil.request(
      method = HttpMethods.GET,
      headers = List(projectsApiAcceptHeader, authHeader),
      path = cardsUrl,
    )
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[Seq[GitHubCard]]
    } yield {
      log.info("Got GitHub cards")
      res
    }
  }

  def getContentOfNote(contentUrl: String): Future[GitHubIssue] = {
    val request = HttpUtil.request(
      method = HttpMethods.GET,
      headers = List(projectsApiAcceptHeader, authHeader),
      path = contentUrl
    )
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[GitHubIssue]
    } yield {
      log.info("Got GitHub issue")
      res
    }
  }

  def getEventsForIssue(eventsUrl: String, page: Int): Future[Seq[GitHubIssueEvent]] = {
    val request = HttpUtil.request(
      method = HttpMethods.GET,
      headers = List(eventsApiAcceptHeader, authHeader),
      path = eventsUrl,
      parameters = gitHubPaginationParams(page)
    )
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[Seq[GitHubIssueEvent]]
    } yield {
      log.info("Got GitHub events")
      res
    }
  }
}
