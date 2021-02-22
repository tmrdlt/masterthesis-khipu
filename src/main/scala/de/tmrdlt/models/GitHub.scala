package de.tmrdlt.models

import de.tmrdlt.database.github.GitHubEventDBEntity
import spray.json.RootJsonFormat

import java.time.LocalDateTime


trait GitHubJsonSupport extends JsonSupport {
  implicit val fetchDataGitHubEntityFormat: RootJsonFormat[FetchDataGitHubEntity] = jsonFormat1(FetchDataGitHubEntity)
  implicit val gitHubBoardFormat: RootJsonFormat[GitHubProject] = jsonFormat10(GitHubProject)
  implicit val gitHubColumnFormat: RootJsonFormat[GitHubColumn] = jsonFormat8(GitHubColumn)
  implicit val gitHubCardFormat: RootJsonFormat[GitHubCard] = jsonFormat10(GitHubCard)
  implicit val gitHubIssueFormat: RootJsonFormat[GitHubIssue] = jsonFormat11(GitHubIssue)
  implicit val gitHubRenameEventFormat: RootJsonFormat[GitHubRenameEvent] = jsonFormat2(GitHubRenameEvent)
  implicit val gitHubEventFormat: RootJsonFormat[GitHubEvent] = jsonFormat8(GitHubEvent)
}

case class FetchDataGitHubEntity(orgNames: Seq[String])

case class GitHubProject(id: Long,
                         node_id: String,
                         name: String,
                         body: String,
                         number: Long,
                         state: String, //TODO maybe enum?
                         url: String,
                         columns_url: String,
                         created_at: LocalDateTime,
                         updated_at: LocalDateTime)

case class GitHubColumn(id: Long,
                        node_id: String,
                        name: String,
                        url: String,
                        cards_url: String,
                        project_url: String,
                        created_at: LocalDateTime,
                        updated_at: LocalDateTime)

case class GitHubCard(id: Long,
                      node_id: String,
                      note: Option[String], // null if card is an issue
                      archived: Boolean,
                      url: String,
                      project_url: String,
                      column_url: String,
                      content_url: Option[String], // contains url to issue if card is issue
                      created_at: LocalDateTime,
                      updated_at: LocalDateTime)

case class GitHubIssue(id: Long,
                       node_id: String,
                       title: String,
                       body: Option[String],
                       number: Long,
                       state: String,
                       locked: Boolean,
                       url: String,
                       events_url: String,
                       created_at: LocalDateTime,
                       updated_at: LocalDateTime)

case class GitHubEvent(id: Long,
                       node_id: String,
                       event: String, // Todo maybe Enum as in Trello Actions
                       url: String,
                       rename: Option[GitHubRenameEvent], // if rename event
                       column_name: Option[String], // if moved_columns_in_project event
                       previous_column_name: Option[String], // if moved_columns_in_project event
                       created_at: LocalDateTime) {
  def toGitHubEventDBEntity(issueId: String): GitHubEventDBEntity = GitHubEventDBEntity(
    id = 0L,
    apiId = id.toString,
    `type` = event,
    issueId = issueId,
    date = created_at
  )
}

case class GitHubRenameEvent(from: String,
                             to: String)