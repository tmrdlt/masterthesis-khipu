package de.tmrdlt.utils

import akka.http.scaladsl.marshalling.ToResponseMarshallable.apply
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.{HttpHeader, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

import scala.collection.immutable

trait PreflightUtil {

  // Handles preflight OPTIONS requests from web browsers
  def preflightRoute: Route =
    options {
      complete(HttpResponse(OK).withHeaders())
    }


  // Add Access-Control-Allow-Headers for web browsers
  def addAccessControlHeaders: Directive0 =
    mapResponseHeaders {
      headers => {
        extraHeaders.foldLeft(headers)(
          (allHeaders, header) =>
            allHeaders.contains(header) match {
              case false => allHeaders :+ header
              case true => allHeaders
            }
        )
      }
    }

  private val extraHeaders: immutable.Seq[HttpHeader] = immutable.Seq(
    `Access-Control-Allow-Origin`.*,
    `Access-Control-Allow-Credentials`(true),
    `Access-Control-Allow-Headers`(
      "Authorization",
      "Content-Type",
      "Cache-Control",
      "Pragma",
      "If-Modified-Since"
    ),
    `Access-Control-Allow-Methods`(DELETE, GET, OPTIONS, PATCH, POST, PUT)
  )
}
