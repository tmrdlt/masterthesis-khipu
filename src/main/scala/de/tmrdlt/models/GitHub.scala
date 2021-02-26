package de.tmrdlt.models

import de.tmrdlt.database.github.GitHubEventDBEntity
import de.tmrdlt.models.GitHubIssueEventType.GitHubIssueEventType
import de.tmrdlt.utils.EnumJsonConverter
import spray.json.RootJsonFormat

import java.time.LocalDateTime


trait GitHubJsonSupport extends JsonSupport {
  implicit val fetchDataGitHubEntityFormat: RootJsonFormat[FetchDataGitHubEntity] = jsonFormat1(FetchDataGitHubEntity)
  implicit val gitHubBoardFormat: RootJsonFormat[GitHubProject] = jsonFormat10(GitHubProject)
  implicit val gitHubColumnFormat: RootJsonFormat[GitHubColumn] = jsonFormat8(GitHubColumn)
  implicit val gitHubCardFormat: RootJsonFormat[GitHubCard] = jsonFormat10(GitHubCard)
  implicit val gitHubIssueFormat: RootJsonFormat[GitHubIssue] = jsonFormat11(GitHubIssue)

  implicit val gitHubIssueEventTypeJsonSupport: EnumJsonConverter[GitHubIssueEventType.type] = new EnumJsonConverter(GitHubIssueEventType)
  implicit val gitHubEventActorFormat: RootJsonFormat[GitHubEventActor] = jsonFormat2(GitHubEventActor)
  implicit val gitHubRenameEventFormat: RootJsonFormat[GitHubRenameEvent] = jsonFormat2(GitHubRenameEvent)
  implicit val gitHubEventProjectCardFormat: RootJsonFormat[GitHubEventProjectCard] = jsonFormat5(GitHubEventProjectCard)
  implicit val gitHubIssueEventFormat: RootJsonFormat[GitHubIssueEvent] = jsonFormat8(GitHubIssueEvent)
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

case class GitHubIssueEvent(id: Long,
                            node_id: String,
                            event: GitHubIssueEventType,
                            url: String,
                            actor: GitHubEventActor,
                            rename: Option[GitHubRenameEvent], // if rename event ???
                            project_card: Option[GitHubEventProjectCard],
                            created_at: LocalDateTime) {
  def toGitHubEventDBEntity(issueId: String): GitHubEventDBEntity = GitHubEventDBEntity(
    id = 0L,
    apiId = id.toString,
    `type` = event.toString,
    issueId = issueId,
    userId = actor.id.toString,
    date = created_at
  )
}

case class GitHubEventProjectCard(id: Long,
                                  url: String,
                                  project_id: Long,
                                  column_name: Option[String], // Present if moved_columns_in_project event
                                  previous_column_name: Option[String] // Present if moved_columns_in_project event
                                 )

case class GitHubEventActor(id: Long,
                            login: String)

case class GitHubRenameEvent(from: String,
                             to: String)

object GitHubIssueEventType extends Enumeration {

  type GitHubIssueEventType = Value
  val added_to_project, assigned, automatic_base_change_failed, automatic_base_change_succeeded, base_ref_changed,
  closed, commented, committed, connected, convert_to_draft, converted_note_to_issue, `cross-referenced`, demilestoned,
  deployed, deployment_environment_changed, disconnected, head_ref_deleted, head_ref_restored, labeled, locked,
  mentioned, marked_as_duplicate, merged, milestoned, moved_columns_in_project, pinned, ready_for_review, referenced,
  removed_from_project, renamed, reopened, review_dismissed, review_requested, review_request_removed, reviewed,
  subscribed, transferred, unassigned, unlabeled, unlocked, unmarked_as_duplicate, unpinned, unsubscribed,
  user_blocked = Value

}