package de.tmrdlt.utils

import scala.concurrent.duration.{Duration, FiniteDuration}

object DurationUtil {

  def stringToDuration(string: String): Duration = Duration(string)

  def stringToFiniteDuration(string: String): FiniteDuration = {
    val duration = stringToDuration(string)
    FiniteDuration(duration.length, duration.unit)
  }
}