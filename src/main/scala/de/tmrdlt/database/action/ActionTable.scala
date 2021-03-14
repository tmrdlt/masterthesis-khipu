package de.tmrdlt.database.action

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.WorkflowListDataSource.WorkflowListDataSource
import slick.lifted.{ProvenShape, Rep, Tag}
import slick.sql.SqlProfile.ColumnOption.{NotNull, Nullable}

import java.time.LocalDateTime

case class Action(id: Long,
                  apiId: String,
                  actionType: String, // TODO make Enum: Decide which actions to use
                  workflowListApiId: String, // TODO make Long and foreign key
                  boardApiId: Option[String], // Only for GitHub & Trello
                  parentApiId: Option[String], // TODO make Long and foreign key // Only for create and delete actions
                  oldParentApiId: Option[String], // TODO make Long and foreign key
                  newParentApiId: Option[String], // Only for move actions
                  userApiId: String,
                  date: LocalDateTime,
                  dataSource: WorkflowListDataSource)

class ActionTable(tag: Tag) extends BaseTableLong[Action](tag, "action") {

  def apiId: Rep[String] = column[String]("api_id", NotNull)

  def actionType: Rep[String] = column[String]("action_type", NotNull)

  def workflowListApiId: Rep[String] = column[String]("workflow_list_api_id", NotNull)

  def boardApiId: Rep[Option[String]] = column[Option[String]]("board_api_id", Nullable)

  def parentApiId: Rep[Option[String]] = column[Option[String]]("parent_api_id", Nullable)

  def oldParentApiId: Rep[Option[String]] = column[Option[String]]("old_parent_api_id", Nullable)

  def newParentApiId: Rep[Option[String]] = column[Option[String]]("new_parent_api_id", Nullable)

  def userApiId: Rep[String] = column[String]("user_api_id", NotNull)

  def date: Rep[LocalDateTime] = column[LocalDateTime]("date", NotNull)

  def dataSource: Rep[WorkflowListDataSource] = column[WorkflowListDataSource]("data_source", NotNull)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def * : ProvenShape[Action] =
    (id, apiId, actionType, workflowListApiId, boardApiId, parentApiId, oldParentApiId, newParentApiId, userApiId, date, dataSource).mapTo[Action]
}