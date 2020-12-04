package de.tmrdlt.models

import de.tmrdlt.models.JsonSupport

import java.util.UUID

trait BoardJsonSupport extends JsonSupport {

  implicit val boardFormat = jsonFormat2(Board)
}

case class Board(UUID: UUID,
                 name: String) {
}
