package de.tmrdlt.components.fetchData.github

import de.tmrdlt.connectors.GitHubApi
import de.tmrdlt.models.{GitHubCard, GitHubColumn, GitHubEvent, GitHubProject, TrelloAction}
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FetchDataGitHubController(gitHubApi: GitHubApi)
  extends SimpleNameLogger with OptionExtensions {

  def fetchDataGitHub(orgNames: Seq[String]): Future[Seq[GitHubEvent]] = {
    for {
      projects <- Future.sequence(orgNames.map(b => gitHubApi.getProjectsOfOrganisation(b))).map(_.flatten)
      columnsOfProjects <- Future.sequence(projects.map(p => gitHubApi.getColumnsOfProject(p.columns_url))).map(_.flatten)
      cardsOfColumns <- Future.sequence(columnsOfProjects.map(c => gitHubApi.getCardsOfColumn(c.cards_url))).map(_.flatten)
      issues <- Future.sequence(
        cardsOfColumns
          .filter(_.content_url.isDefined)
          .map(c => gitHubApi.getContentOfNote(c.content_url.getOrException("error getting option")))
      )
      events <- Future.sequence(issues.map(i => gitHubApi.getEventsForIssue(i.events_url))).map(_.flatten)
    } yield {
      events.filter(i => isRelevantEvent(i.event))
    }
  }

  // https://docs.github.com/en/developers/webhooks-and-events/issue-event-types
  private def isRelevantEvent(event: String): Boolean = {
    event match {
      case "added_to_project" => true
      case "moved_columns_in_project" => true
      case "removed_from_project" => true
      case "renamed" => true
      case _ => false
    }
  }
}