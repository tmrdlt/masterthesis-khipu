package de.tmrdlt.connectors

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.{Http, HttpExt}
import de.tmrdlt.models.TrelloActionType.TrelloActionType
import de.tmrdlt.models.{TrelloAction, TrelloBoard, TrelloCard, TrelloJsonSupport, TrelloList}
import de.tmrdlt.utils.{SimpleNameLogger, WorkflowConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class TrelloApi(implicit system: ActorSystem) extends SimpleNameLogger with WorkflowConfig with TrelloJsonSupport {


  val trelloApiKey = "a321bfc00e9cd2c02d68dfaa73f9720d"
  val trelloApiToken = "89ad3e0de3fa575edc26a6d748e2d2bdb5440130398363ef8998c935c3ea4552"

  val authUrl = s"?key=${trelloApiKey}&token=${trelloApiToken}"
  val baseUrl = "https://api.trello.com/"

  val http: HttpExt = Http()

  def getBoard(boardId: String): Future[TrelloBoard] = {
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = s"${baseUrl}1/boards/${boardId}${authUrl}"
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
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = s"${baseUrl}1/boards/${boardId}/lists${authUrl}"
    )
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[Seq[TrelloList]]
    } yield {
      log.info("Got lists")
      res
    }
  }

  def getCardsInAList(listId: String): Future[Seq[TrelloCard]] = {
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = s"${baseUrl}1/lists/${listId}/cards${authUrl}"
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
    val beforeUrl = beforeId match {
      case Some(string) => s"&before=${string}"
      case _ => ""
    }
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = s"${baseUrl}1/boards/${boardId}/actions${authUrl}${beforeUrl}"
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
