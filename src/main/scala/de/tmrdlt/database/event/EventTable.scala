package de.tmrdlt.database.event

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.WorkflowListDataSource.WorkflowListDataSource
import de.tmrdlt.models.WorkflowListType.WorkflowListType
import de.tmrdlt.models.{TemporalQueryResultEntity, WorkflowListQueryJsonSupport}
import slick.ast.BaseTypedType
import slick.jdbc.JdbcType
import slick.lifted.{ProvenShape, Rep, Tag}
import slick.sql.SqlProfile.ColumnOption.{NotNull, Nullable}
import spray.json.{JsValue, _}

import java.time.LocalDateTime

/**
 *
 * @param id                  Database id
 * @param apiId               UUID (generated upon insert) to be used as a unique identifier when the real db id is
 *                            not desired.
 * @param eventType           TBD
 * @param workflowListApiId   TBD
 * @param boardApiId          Only for GitHub & Trello
 * @param parentApiId         Only for CREATE and DELETE event
 * @param oldParentApiId      Only for MOVE event
 * @param newParentApiId      Only for MOVE event
 * @param oldPosition         Only for REORDER event
 * @param newPosition         Only for REORDER event
 * @param newType             Only for CONVERT event
 * @param resourcesUpdated    Only for or UPDATE_RESOURCES
 * @param temporalQueryResult Only for TEMPORAL_QUERY event
 * @param userApiId           ApiId of the user that performed the action
 * @param createdAt           Date at which the action was performed
 * @param dataSource          Data source: Khipu / Trello / Github
 */
case class Event(id: Long,
                 apiId: String,
                 eventType: String, // TODO make Enum: Decide which actions to use
                 workflowListApiId: String, // TODO make Long and foreign key
                 boardApiId: Option[String] = None, //
                 parentApiId: Option[String] = None, //  TODO make Long and foreign key
                 oldParentApiId: Option[String] = None, // TODO make Long and foreign key
                 newParentApiId: Option[String] = None,
                 oldPosition: Option[Long] = None,
                 newPosition: Option[Long] = None,
                 newType: Option[WorkflowListType] = None,
                 resourcesUpdated: Option[Long] = None,
                 temporalQueryResult: Option[TemporalQueryResultEntity] = None,
                 userApiId: String,
                 createdAt: LocalDateTime,
                 dataSource: WorkflowListDataSource)

trait EventSlickExtension extends WorkflowListQueryJsonSupport {
  implicit val temporalQueryResultMapper: JdbcType[TemporalQueryResultEntity] with BaseTypedType[TemporalQueryResultEntity] =
    MappedColumnType.base[TemporalQueryResultEntity, JsValue](_.toJson, _.convertTo[TemporalQueryResultEntity])
}

class EventTable(tag: Tag) extends BaseTableLong[Event](tag, "event") with EventSlickExtension {

  def apiId: Rep[String] = column[String]("api_id", NotNull)

  def eventType: Rep[String] = column[String]("event_type", NotNull)

  def workflowListApiId: Rep[String] = column[String]("workflow_list_api_id", NotNull)

  def boardApiId: Rep[Option[String]] = column[Option[String]]("board_api_id", Nullable)

  def parentApiId: Rep[Option[String]] = column[Option[String]]("parent_api_id", Nullable)

  def oldParentApiId: Rep[Option[String]] = column[Option[String]]("old_parent_api_id", Nullable)

  def newParentApiId: Rep[Option[String]] = column[Option[String]]("new_parent_api_id", Nullable)

  def oldPosition: Rep[Option[Long]] = column[Option[Long]]("old_position", Nullable)

  def newPosition: Rep[Option[Long]] = column[Option[Long]]("new_position", Nullable)

  def newType: Rep[Option[WorkflowListType]] = column[Option[WorkflowListType]]("new_type", Nullable)

  def resourcesUpdated: Rep[Option[Long]] = column[Option[Long]]("resources_updated", Nullable)

  def temporalQueryResult: Rep[Option[TemporalQueryResultEntity]] = column[Option[TemporalQueryResultEntity]]("temporal_query_result", Nullable)

  def userApiId: Rep[String] = column[String]("user_api_id", NotNull)

  def createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at", NotNull)

  def dataSource: Rep[WorkflowListDataSource] = column[WorkflowListDataSource]("data_source", NotNull)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def * : ProvenShape[Event] = (
    id,
    apiId,
    eventType,
    workflowListApiId,
    boardApiId,
    parentApiId,
    oldParentApiId,
    newParentApiId,
    oldPosition,
    newPosition,
    newType,
    resourcesUpdated,
    temporalQueryResult,
    userApiId,
    createdAt,
    dataSource
  ) <> (Event.tupled, Event.unapply)
}