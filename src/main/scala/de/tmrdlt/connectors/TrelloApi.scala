package de.tmrdlt.connectors

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.{Http, HttpExt}
import de.tmrdlt.models._
import de.tmrdlt.utils.{HttpUtil, SimpleNameLogger, WorkflowConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class TrelloApi(implicit system: ActorSystem) extends SimpleNameLogger with WorkflowConfig with TrelloJsonSupport {
  val http: HttpExt = Http()

  // Base URL
  // https://developer.atlassian.com/cloud/trello/guides/rest-api/api-introduction/#your-first-api-call
  val baseUrl = "https://api.trello.com/1"

  // Authentication
  // https://developer.atlassian.com/cloud/trello/guides/rest-api/api-introduction/#your-first-api-call
  val trelloApiKey = "a321bfc00e9cd2c02d68dfaa73f9720d"
  val trelloApiToken = "89ad3e0de3fa575edc26a6d748e2d2bdb5440130398363ef8998c935c3ea4552"
  val trelloAuthParams = Seq(("key", trelloApiKey), ("token", trelloApiToken))

  // Pagination
  // https://developer.atlassian.com/cloud/trello/guides/rest-api/api-introduction/#paging

  def getBoard(boardId: String): Future[TrelloBoard] = {
    val request = HttpUtil.request(
      method = HttpMethods.GET,
      path = s"${baseUrl}/boards/$boardId",
      parameters = trelloAuthParams
    )
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[TrelloBoard]
    } yield {
      log.info("Got board")
      res
    }
  }

  def getListOnABoard(boardId: String): Future[Seq[TrelloList]] = {
    val request = HttpUtil.request(
      method = HttpMethods.GET,
      path = s"${baseUrl}/boards/${boardId}/lists",
      parameters = trelloAuthParams
    )
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[Seq[TrelloList]]
    } yield {
      log.info("Got lists")
      res
    }
  }

  // TODO implement pagination
  def getCardsInAList(listId: String): Future[Seq[TrelloCard]] = {
    val request = HttpUtil.request(
      method = HttpMethods.GET,
      path = s"${baseUrl}/lists/${listId}/cards",
      parameters = trelloAuthParams
    )
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[Seq[TrelloCard]]
    } yield {
      log.info("Got cards")
      res
    }
  }

  def getActionsOfABoard(boardId: String, beforeId: Option[String]): Future[Seq[TrelloAction]] = {
    //val filterUrl = s"&filter=${desiredActions.map(_.toString).mkString(",")}"
    val beforeParam = beforeId match {
      case Some(string) => Seq(("before", string))
      case _ => Seq.empty
    }
    val request = HttpUtil.request(
      method = HttpMethods.GET,
      path = s"${baseUrl}//boards/${boardId}/actions",
      parameters = trelloAuthParams ++ beforeParam
    )
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[Seq[TrelloAction]]
    } yield {
      log.info("Got actions")
      res
    }
  }

}
