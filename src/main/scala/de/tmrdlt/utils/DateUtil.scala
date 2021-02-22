package de.tmrdlt.utils

import org.mongodb.scala.bson.BsonObjectId

import java.time.{Instant, LocalDateTime}
import java.util.TimeZone


object DateUtil {

  def getDateFromObjectIdString(objectId: String): LocalDateTime =
    LocalDateTime.ofInstant(
      Instant.ofEpochSecond(BsonObjectId(objectId).getValue.getTimestamp),
      TimeZone.getDefault.toZoneId
    )
}
