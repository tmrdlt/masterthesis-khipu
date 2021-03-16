package de.tmrdlt.database.action

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.WorkflowListDataSource.WorkflowListDataSource
import slick.lifted.{ProvenShape, Rep, Tag}
import slick.sql.SqlProfile.ColumnOption.{NotNull, Nullable}

import java.time.LocalDateTime

case class Event(id: Long,
                 apiId: String,
                 eventType: String, // TODO make Enum: Decide which actions to use
                 workflowListApiId: String, // TODO make Long and foreign key
                 boardApiId: Option[String], // Only for GitHub & Trello
                 parentApiId: Option[String], // TODO make Long and foreign key // Only for create and delete actions
                 oldParentApiId: Option[String], // TODO make Long and foreign key
                 newParentApiId: Option[String], // Only for move actions
                 userApiId: String,
                 date: LocalDateTime,
                 dataSource: WorkflowListDataSource)

class EventTable(tag: Tag) extends BaseTableLong[Event](tag, "event") {

  def apiId: Rep[String] = column[String]("api_id", NotNull)

  def eventType: Rep[String] = column[String]("event_type", NotNull)

  def workflowListApiId: Rep[String] = column[String]("workflow_list_api_id", NotNull)

  def boardApiId: Rep[Option[String]] = column[Option[String]]("board_api_id", Nullable)

  def parentApiId: Rep[Option[String]] = column[Option[String]]("parent_api_id", Nullable)

  def oldParentApiId: Rep[Option[String]] = column[Option[String]]("old_parent_api_id", Nullable)

  def newParentApiId: Rep[Option[String]] = column[Option[String]]("new_parent_api_id", Nullable)

  def userApiId: Rep[String] = column[String]("user_api_id", NotNull)

  def date: Rep[LocalDateTime] = column[LocalDateTime]("date", NotNull)

  def dataSource: Rep[WorkflowListDataSource] = column[WorkflowListDataSource]("data_source", NotNull)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def * : ProvenShape[Event] =
    (id, apiId, eventType, workflowListApiId, boardApiId, parentApiId, oldParentApiId, newParentApiId, userApiId, date, dataSource).mapTo[Event]
}