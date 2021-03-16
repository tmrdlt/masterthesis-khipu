package de.tmrdlt.components.fetchData.github

import akka.actor.ActorRef
import de.tmrdlt.components.fetchData.FetchDataActor.FetchDataGitHub
import de.tmrdlt.models.FetchDataGitHubEntity
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}


class FetchDataGitHubController(fetchDataActor: ActorRef)
  extends SimpleNameLogger with OptionExtensions {

  def fetchDataGitHub(fetchDataGitHubEntity: FetchDataGitHubEntity): Unit = {
    fetchDataActor ! FetchDataGitHub(fetchDataGitHubEntity.ownerAndRepo)
  }
}