package de.tmrdlt.connectors

import akka.actor.ActorSystem

class Apis(system: ActorSystem) {

  val trelloApi: TrelloApi = new TrelloApi()(system)
  val gitHubApi: GitHubApi = new GitHubApi()(system)

}
