package de.tmrdlt.utils

import com.typesafe.config.{Config, ConfigFactory}


trait WorkflowConfig {

  val config: Config = WorkflowConfig.config
}


object WorkflowConfig {

  private val config: Config = ConfigFactory.load()
}
