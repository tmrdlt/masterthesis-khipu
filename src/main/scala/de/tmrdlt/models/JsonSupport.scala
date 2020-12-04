package de.tmrdlt.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.NullOptions

object JsonSupport extends JsonSupport

trait JsonSupport extends SprayJsonSupport with AdditionalJsonTypes with NullOptions