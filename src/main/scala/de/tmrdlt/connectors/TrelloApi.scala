package de.tmrdlt.connectors

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.{Http, HttpExt}
import de.tmrdlt.models.{TrelloBoard, TrelloCard, TrelloJsonSupport, TrelloList}
import de.tmrdlt.utils.WorkflowConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class TrelloApi(implicit system: ActorSystem) extends WorkflowConfig with TrelloJsonSupport {


  val trelloApiKey = "a321bfc00e9cd2c02d68dfaa73f9720d"
  val trelloApiToken = "89ad3e0de3fa575edc26a6d748e2d2bdb5440130398363ef8998c935c3ea4552"

  val authUrl = s"?key=${trelloApiKey}&token${trelloApiToken}"
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
      res
    }
  }

}
