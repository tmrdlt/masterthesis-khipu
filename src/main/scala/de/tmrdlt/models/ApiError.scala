package de.tmrdlt.models

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes._
import spray.json.{JsObject, JsString, JsValue, RootJsonFormat, deserializationError, _}

import scala.language.implicitConversions

trait ApiErrorJsonSupport extends JsonSupport {

  implicit def exceptionAsApiError(e: Throwable): ApiError = new ApiError(e)

  implicit def errorFormat: RootJsonFormat[ApiError] = new RootJsonFormat[ApiError] {

    def write(e: ApiError): JsValue =
      e.message match {
        case Some(message) => JsObject(
          "errorType" -> JsString(e.errorType.toString),
          "errorMessage" -> message.toJson
        )
        case None => JsObject(
          "errorType" -> JsString(e.errorType.toString)
        )
      }

    def read(value: JsValue): ApiError =
      deserializationError("ServerException in not going to be deserialized")
  }
}


sealed trait ErrorKey

case object NOT_FOUND extends ErrorKey

case object CONFLICT extends ErrorKey

case object INTERNAL_SERVER_ERROR extends ErrorKey

case object INVALID_INPUT extends ErrorKey

case object MISMATCH extends ErrorKey

case object EXTERNAL_API_ERROR extends ErrorKey

case object JSON_ERROR extends ErrorKey


case class ApiError(errorType: ErrorKey,
                    message: Option[String])
  extends Exception(errorType.toString + " " + message.getOrElse(""))
    with ApiErrorJsonSupport {

  def this(k: ErrorKey, m: String) =
    this(k, Some(m))

  def this(k: ErrorKey) =
    this(k, None)

  def this(e: Throwable) =
    this(
      e match {
        case ApiError(k, m) => k
        case otherException: Throwable => INTERNAL_SERVER_ERROR
      },
      e match {
        case ApiError(k, m) => m
        case otherException: Throwable => Some(otherException.getMessage)
      }
    )


  def statusCode: StatusCode = errorType match {
    case NOT_FOUND => NotFound
    case CONFLICT => Conflict
    case INTERNAL_SERVER_ERROR => InternalServerError
    case INVALID_INPUT => BadRequest
    case MISMATCH => BadRequest
    case EXTERNAL_API_ERROR => ServiceUnavailable
    case JSON_ERROR => InternalServerError
    case _ => InternalServerError
  }

  def toResponseMarshallable: (StatusCode, JsValue) = this.statusCode -> this.toJson

  override def toString: String = this.toJson.toString
}