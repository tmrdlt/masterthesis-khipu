package de.tmrdlt.database.github

import de.tmrdlt.database.{BaseTableLong, BaseTableString}
import de.tmrdlt.database.MyPostgresProfile.api._
import slick.lifted.{ProvenShape, Rep, Tag}
import slick.sql.SqlProfile.ColumnOption.NotNull

import java.time.LocalDateTime

case class GitHubEventDBEntity(id: Long,
                               apiId: String,
                               `type`: String,
                               issueId: String,
                               userId: String,
                               date: LocalDateTime)

class GitHubEventTable(tag: Tag) extends BaseTableLong[GitHubEventDBEntity](tag, "github_event") {

  def apiId: Rep[String] = column[String]("api_id", NotNull)

  def `type`: Rep[String] = column[String]("type", NotNull)

  def issueId: Rep[String] = column[String]("issue_id", NotNull)

  def userId: Rep[String] = column[String]("user_id", NotNull)

  def date: Rep[LocalDateTime] = column[LocalDateTime]("date", NotNull)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def * : ProvenShape[GitHubEventDBEntity] =
    (id, apiId, `type`, issueId, userId, date).mapTo[GitHubEventDBEntity]
}