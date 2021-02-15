package de.tmrdlt.connectors

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.{Accept, Authorization, BasicHttpCredentials}
import akka.http.scaladsl.model.{HttpHeader, HttpMethods, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.{Http, HttpExt}
import de.tmrdlt.models.{GitHubCard, GitHubColumn, GitHubEvent, GitHubIssue, GitHubJsonSupport, GitHubProject}
import de.tmrdlt.utils.WorkflowConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GitHubApi(implicit system: ActorSystem) extends WorkflowConfig with GitHubJsonSupport {

  val gitHubPersonalAccessToken = "af9254f3fba08697765c940400061b85c070a0a4"
  val gitHubUsername = "timunkulus"

  val projectsAcceptHeaderValue = "application/vnd.github.inertia-preview+json"
  val baseUrl = "https://api.github.com/"

  val acceptHeader: HttpHeader = Accept.parseFromValueString(projectsAcceptHeaderValue) match {
    case Right(header) => header
    case Left(_) => throw new Exception("Error parsing accept header")
  }
  val authHeader: HttpHeader = Authorization(BasicHttpCredentials(gitHubUsername, gitHubPersonalAccessToken))

  val http: HttpExt = Http()

  def getProjectsOfOrganisation(orgName: String): Future[Seq[GitHubProject]] = {
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = s"${baseUrl}orgs/${orgName}/projects",
      headers = List(acceptHeader)
    )
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[Seq[GitHubProject]]
    } yield {
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
      res
    }
  }

  def getEventsForIssue(events_url: String): Future[Seq[GitHubEvent]] = {
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = events_url,
      headers = List(acceptHeader, authHeader)
    )
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[Seq[GitHubEvent]]
    } yield {
      res
    }
  }
}