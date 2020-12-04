package de.tmrdlt.utils

import org.slf4j.{Logger, LoggerFactory}


trait SimpleNameLogger {

  val log: Logger = LoggerFactory.getLogger(getClass.getSimpleName)
}