package de.tmrdlt.models

import akka.http.scaladsl.model.RemoteAddress.IP
import akka.http.scaladsl.model.{ContentType, RemoteAddress, StatusCode}
import spray.json._

import java.net.InetAddress
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, OffsetDateTime, ZoneOffset}
import java.util.UUID
import scala.util.{Failure, Success, Try}


object AdditionalJsonTypes extends AdditionalJsonTypes

trait AdditionalJsonTypes extends DefaultJsonProtocol {

  implicit object LocalDateJsonFormat extends RootJsonFormat[LocalDate] {
    override def write(date: LocalDate): JsString = JsString(date.toString)

    override def read(value: JsValue): LocalDate = value match {
      case JsString(date) => LocalDate.parse(date)
      case _ => LocalDate.MIN
    }
  }

  implicit object LocalDateTimeJsonFormat extends RootJsonFormat[LocalDateTime] {
    override def write(timestamp: LocalDateTime): JsString = JsString(timestamp.toString)

    override def read(value: JsValue): LocalDateTime = value match {
      case JsString(timestamp) => Try(LocalDateTime.parse(timestamp)) match {
        case Success(res) => res
        case Failure(_) => LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME)
      }
      case _ => LocalDateTime.MIN
    }
  }

  implicit object OffsetDateTimeJsonFormat extends RootJsonFormat[OffsetDateTime] {
    override def write(dateTime: OffsetDateTime): JsString = JsString(dateTime.toString)

    override def read(value: JsValue): OffsetDateTime = value match {
      case JsString(timeStamp) => Try(OffsetDateTime.parse(timeStamp)) match {
        case Success(res) => res
        case Failure(_) =>
          OffsetDateTime.parse(timeStamp, DateTimeFormatter.ISO_DATE_TIME)
      }
      case _ => OffsetDateTime.of(LocalDateTime.MIN, ZoneOffset.UTC)
    }
  }

  implicit object UUIDJsonFormat extends RootJsonFormat[UUID] {
    override def write(uuid: UUID): JsString = JsString(uuid.toString)

    override def read(value: JsValue): UUID = value match {
      case JsString(uuid) => UUID.fromString(uuid)
      case _ => deserializationError("Cannot parse uuid from string: " + value)
    }
  }

  implicit object StatusCodeJsonFormat extends RootJsonFormat[StatusCode] {
    override def write(statusCode: StatusCode): JsString = JsString(statusCode.toString)

    override def read(value: JsValue): StatusCode = value match {
      case JsString(statusCodeStr) => StatusCode.int2StatusCode(Integer.parseInt(statusCodeStr))
      case JsNumber(statusCodeNum) => StatusCode.int2StatusCode(statusCodeNum.toInt)
      case _ => deserializationError("Cannot parse status code from string: " + value)
    }
  }

  implicit object ContentTypeFormat extends RootJsonFormat[ContentType] {
    override def write(contentType: ContentType): JsString = JsString(contentType.mediaType.toString())

    override def read(value: JsValue): ContentType =
      deserializationError(s"Can and should not deserialize content type from ${value.toString()}")
  }

  implicit object RemoteAddressFormat extends RootJsonFormat[RemoteAddress] {
    override def write(remoteAddress: RemoteAddress): JsString = JsString(remoteAddress.toString())

    override def read(value: JsValue): RemoteAddress = value match {
      case JsString(remoteAddress) =>
        if (remoteAddress.contains(':')) {
          val hostPort = remoteAddress.split(':')
          IP(InetAddress.getByName(hostPort(0)), Some(hostPort(1).toInt))
        } else {
          RemoteAddress(InetAddress.getByName(remoteAddress))
        }
      case _ => deserializationError("Cannot parse remoteAddress from string: " + value)
    }
  }
}
