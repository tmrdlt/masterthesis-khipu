package de.tmrdlt.utils

import akka.util.Timeout
import scala.concurrent.duration.FiniteDuration

trait ActorTimeout {

  implicit def timeout: Timeout = ActorTimeout.timeout
}

object ActorTimeout extends WorkflowConfig {

  val timeout: Timeout = {
    val timeout: String = config.getString("akka.http.server.request-timeout")
    val duration: FiniteDuration = DurationUtil.stringToFiniteDuration(timeout)
    Timeout(FiniteDuration(duration.length, duration.unit))
  }
}
