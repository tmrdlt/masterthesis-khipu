package de.tmrdlt.models

import spray.json.RootJsonFormat

import java.time.LocalDateTime

trait TrelloJsonSupport extends JsonSupport {
  implicit val fetchTrelloBoardsEntity: RootJsonFormat[FetchTrelloBoardsEntity] = jsonFormat1(FetchTrelloBoardsEntity)
  implicit val trelloBoardFormat: RootJsonFormat[TrelloBoard] = jsonFormat4(TrelloBoard)
  implicit val trelloListFormat: RootJsonFormat[TrelloList] = jsonFormat5(TrelloList)
  implicit val trelloCardFormat: RootJsonFormat[TrelloCard] = jsonFormat7(TrelloCard)
}

case class FetchTrelloBoardsEntity(boardIds: Seq[String])

case class TrelloBoard(id: String,
                       desc: String,
                       closed: Boolean,
                       url: String)

case class TrelloList(id: String,
                      name: String,
                      closed: Boolean,
                      pos: Long,
                      idBoard: String)

case class TrelloCard(id: String,
                      closed: Boolean,
                      dateLastActivity: LocalDateTime,
                      desc: String,
                      idBoard: String,
                      idList: String,
                      name: String)