package de.tmrdlt.models

import spray.json.RootJsonFormat

trait TrelloJsonSupport extends JsonSupport {
  implicit val trelloBoardFormat: RootJsonFormat[TrelloBoard] = jsonFormat4(TrelloBoard)
}

case class TrelloBoard(id: String,
                       desc: String,
                       closed: Boolean,
                       url: String)
