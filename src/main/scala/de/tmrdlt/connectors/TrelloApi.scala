package de.tmrdlt.connectors

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.{Http, HttpExt}
import de.tmrdlt.models._
import de.tmrdlt.utils.{DateUtil, FutureUtil, HttpUtil, OptionExtensions, SimpleNameLogger, WorkflowConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class TrelloApi(implicit system: ActorSystem) extends SimpleNameLogger
  with WorkflowConfig
  with TrelloJsonSupport
  with OptionExtensions {

  private val http: HttpExt = Http()

  // Base URL
  // https://developer.atlassian.com/cloud/trello/guides/rest-api/api-introduction/#your-first-api-call
  private val baseUrl = "https://api.trello.com/1"

  // Authentication
  // https://developer.atlassian.com/cloud/trello/guides/rest-api/api-introduction/#your-first-api-call
  private val trelloApiKey = "a321bfc00e9cd2c02d68dfaa73f9720d"
  private val trelloApiToken = "89ad3e0de3fa575edc26a6d748e2d2bdb5440130398363ef8998c935c3ea4552"
  private val trelloAuthParams = Seq(("key", trelloApiKey), ("token", trelloApiToken))

  // Pagination
  // https://developer.atlassian.com/cloud/trello/guides/rest-api/api-introduction/#paging
  private def trelloPaginationParam(beforeId: Option[String]): Seq[(String, String)] =
    beforeId match {
      case Some(string) => Seq(("before", string))
      case _ => Seq.empty
    }

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

  def getListsOfBoard(boardId: String): Future[Seq[TrelloList]] = {
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

  def getAllCardsOfList(boardId: String): Future[Seq[TrelloCard]] = {
    getCardsOfListRecursively(boardId, None)
  }

  def getAllActionsOfBoard(boardId: String): Future[Seq[TrelloAction]] = {
    getActionsOfBoardRecursively(boardId, None)
  }

  private def getCardsOfListRecursively(listId: String, beforeId: Option[String]): Future[Seq[TrelloCard]] = {
    getCardsOfList(listId, beforeId).flatMap { seq =>
      if (seq.nonEmpty) FutureUtil.mergeFutureSeqs(
        getCardsOfListRecursively(listId, Some(seq.sortBy(c => DateUtil.getDateFromObjectIdString(c.id)).headOption.map(_.id).getOrException("Something almost impossible happened"))), Future.successful(seq))
      else Future.successful(seq)
    }
  }

  private def getCardsOfList(listId: String, beforeId: Option[String]): Future[Seq[TrelloCard]] = {
    val request = HttpUtil.request(
      method = HttpMethods.GET,
      path = s"${baseUrl}/lists/${listId}/cards",
      parameters = trelloAuthParams ++ trelloPaginationParam(beforeId)
    )
    for {
      response <- http.singleRequest(request)
      res <- Unmarshal(response).to[Seq[TrelloCard]]
    } yield {
      log.info("Got cards")
      res
    }
  }

  private def getActionsOfBoardRecursively(boardId: String, beforeId: Option[String]): Future[Seq[TrelloAction]] = {
    getActionsOfBoard(boardId, beforeId).flatMap { seq =>
      if (seq.nonEmpty) FutureUtil.mergeFutureSeqs(
        getActionsOfBoardRecursively(boardId, Some(seq.sortBy(_.date).headOption.map(_.id).getOrException("Something almost impossible happened"))), Future.successful(seq))
      else Future.successful(seq)
    }
  }

  private def getActionsOfBoard(boardId: String, beforeId: Option[String]): Future[Seq[TrelloAction]] = {
    //val filterUrl = s"&filter=${desiredActions.map(_.toString).mkString(",")}"
    val request = HttpUtil.request(
      method = HttpMethods.GET,
      path = s"${baseUrl}/boards/${boardId}/actions",
      parameters = trelloAuthParams ++ trelloPaginationParam(beforeId)
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
