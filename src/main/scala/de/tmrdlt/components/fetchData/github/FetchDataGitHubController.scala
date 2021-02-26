package de.tmrdlt.components.fetchData.github

import akka.actor.ActorRef
import de.tmrdlt.components.fetchData.FetchDataActor.FetchGitHubDataForProject
import de.tmrdlt.connectors.GitHubApi
import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FetchDataGitHubController(gitHubApi: GitHubApi,
                                workflowListDB: WorkflowListDB,
                                fetchDataActor: ActorRef)
  extends SimpleNameLogger with OptionExtensions {

  def fetchDataGitHub(orgNames: Seq[String]): Unit = {
    log.info("Start fetching GitHub data...")
    (for {
      projectsLists <- Future.sequence(orgNames.map(b => gitHubApi.getProjectsForRepository(b)))
    } yield {
      log.info(s"Got ${projectsLists.flatten.length} projects.")
      projectsLists.flatMap(_.zipWithIndex.map { case (project, index) =>
        fetchDataActor ! FetchGitHubDataForProject(project, index)
      })
    }).recoverWith {
      case t: Throwable => log.error(s"Error fetching projects", t)
        Future.failed(t)
    }

  }
}