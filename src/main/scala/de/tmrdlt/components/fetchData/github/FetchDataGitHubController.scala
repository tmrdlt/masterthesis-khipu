package de.tmrdlt.components.fetchData.github

import de.tmrdlt.connectors.GitHubApi
import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListDB}
import de.tmrdlt.models.WorkflowListState.getWorkflowListState
import de.tmrdlt.models.{WorkflowListDataSource, WorkflowListState, WorkflowListType, WorkflowListUseCase}
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FetchDataGitHubController(gitHubApi: GitHubApi,
                                workflowListDB: WorkflowListDB)
  extends SimpleNameLogger with OptionExtensions {

  def fetchDataGitHub(orgNames: Seq[String]): Future[Int] = {
    val now = LocalDateTime.now()

    for {
      projects <- Future.sequence(orgNames.map(b => gitHubApi.getProjectsOfOrganisation(b))).map(_.flatten)
      columnsOfProjects <- Future.sequence(projects.map(p => gitHubApi.getColumnsOfProject(p.columns_url))).map(_.flatten)
      cardsOfColumns <- Future.sequence(columnsOfProjects.map(c => gitHubApi.getCardsOfColumn(c.cards_url))).map(_.flatten)
      issues <- Future.sequence(
        cardsOfColumns
          .map { card =>
            (card.note, card.content_url) match {
              case (Some(_), None) => Future.successful(card, None) // Card is a note
              case (None, Some(content_url)) => gitHubApi.getContentOfNote(content_url).map(i => (card, Some(i))) // Card is an issue
              case _ => Future.failed(new Exception("Error fetching github data. Card was neither issue nor note"))
            }
          }
      )

      insertedProjects <- workflowListDB.insertWorkflowLists(projects.map { gitHubProject =>
        WorkflowList(
          id = 0L,
          apiId = gitHubProject.id.toString,
          title = gitHubProject.name,
          description = Some(gitHubProject.body),
          parentId = None,
          position = 0,
          listType = WorkflowListType.BOARD,
          state = getWorkflowListState(gitHubProject.state),
          dataSource = WorkflowListDataSource.GitHub,
          useCase = Some(WorkflowListUseCase.softwareDevelopment), // TODO Add to request
          createdAt = gitHubProject.created_at,
          updatedAt = gitHubProject.updated_at
        )
      })

      insertedColumns <- workflowListDB.insertWorkflowLists(columnsOfProjects.map { gitHubColumn =>
        WorkflowList(
          id = 0L,
          apiId = gitHubColumn.id.toString,
          title = gitHubColumn.name,
          description = None,
          parentId = insertedProjects.find(_.apiId == gitHubColumn.project_url.split("/").last).map(_.id),
          position = 0, // TODO how to get?
          listType = WorkflowListType.LIST,
          state = Some(WorkflowListState.OPEN), // Columns only exist on GitHub only in the OPEN state
          dataSource = WorkflowListDataSource.GitHub,
          useCase = Some(WorkflowListUseCase.softwareDevelopment), // TODO Add to request
          createdAt = gitHubColumn.created_at,
          updatedAt = gitHubColumn.updated_at
        )
      })

      insertedIssues <- workflowListDB.insertWorkflowLists(issues.map {
        case (gitHubCard, Some(gitHubIssue)) =>
          WorkflowList(
            id = 0L,
            apiId = gitHubIssue.id.toString,
            title = gitHubIssue.title,
            description = gitHubIssue.body,
            parentId = insertedColumns.find(_.apiId == gitHubCard.column_url.split("/").last).map(_.id),
            position = 0,
            listType = WorkflowListType.ITEM,
            state = getWorkflowListState(gitHubIssue.state),
            dataSource = WorkflowListDataSource.GitHub,
            useCase = Some(WorkflowListUseCase.softwareDevelopment), // TODO Add to request
            createdAt = gitHubIssue.created_at,
            updatedAt = gitHubIssue.updated_at
          )
        case (gitHubCard, None) =>
          WorkflowList(
            id = 0L,
            apiId = gitHubCard.id.toString,
            title = gitHubCard.note.getOrException("Error taking note of card"),
            description = None,
            parentId = insertedColumns.find(_.apiId == gitHubCard.column_url.split("/").last).map(_.id),
            position = 0,
            listType = WorkflowListType.ITEM,
            state = None,
            dataSource = WorkflowListDataSource.GitHub,
            useCase = Some(WorkflowListUseCase.softwareDevelopment), // TODO Add to request
            createdAt = gitHubCard.created_at,
            updatedAt = gitHubCard.updated_at
          )
      })

      // events <- Future.sequence(issues.map(i => gitHubApi.getEventsForIssue(i.events_url))).map(_.flatten)
    } yield {
      insertedProjects.length + insertedColumns.length + insertedIssues.length
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