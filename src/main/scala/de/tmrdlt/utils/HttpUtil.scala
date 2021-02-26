package de.tmrdlt.utils

import akka.http.scaladsl.model.Uri.{Path, Query}
import akka.http.scaladsl.model.{HttpHeader, HttpMethod, HttpRequest, Uri}
import akka.http.scaladsl.model.headers.Accept

object HttpUtil {

  def parseAcceptHeader(acceptHeaderValue: String): HttpHeader =
    Accept.parseFromValueString(acceptHeaderValue) match {
      case Right(header) => header
      case Left(_) => throw new Exception("Error parsing accept header")
    }

  def request(method: HttpMethod,
              headers: List[HttpHeader] = List.empty,
              path: String,
              parameters: Seq[(String, String)] = Seq.empty): HttpRequest = {
    val uri = Uri(path).withQuery(Query(parameters: _*))
    HttpRequest(method = method, headers = headers).withUri(uri)
  }

}
