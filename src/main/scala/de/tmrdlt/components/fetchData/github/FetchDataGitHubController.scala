package de.tmrdlt.components.fetchData.github

import akka.actor.ActorRef
import de.tmrdlt.components.fetchData.FetchDataActor.FetchDataGitHub
import de.tmrdlt.connectors.GitHubApi
import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}

import java.time.LocalDateTime


class FetchDataGitHubController(gitHubApi: GitHubApi,
                                workflowListDB: WorkflowListDB,
                                fetchDataActor: ActorRef)
  extends SimpleNameLogger with OptionExtensions {

  def fetchDataGitHub(orgNames: Seq[String]): Unit = {
    log.info("Start fetching GitHub data...")
    val now = LocalDateTime.now()
    fetchDataActor ! FetchDataGitHub(orgNames, now)
  }
}