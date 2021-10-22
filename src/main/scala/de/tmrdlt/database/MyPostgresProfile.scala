package de.tmrdlt.database

import com.github.tminglei.slickpg._
import de.tmrdlt.models._
import de.tmrdlt.utils.SimpleNameLogger
import slick.util.SlickLogger

trait MyPostgresProfile
  extends SimpleNameLogger // Must be first so log is already there for ExPostgresProfile
    with ExPostgresProfile
    with PgDate2Support
    with PgEnumSupport
    with PgArraySupport
    with PgSprayJsonSupport {

  override val api: MyPostgresApi.type = MyPostgresApi
  override lazy val logger: SlickLogger = new SlickLogger(log)

  def pgjson: String = "json"

  object MyPostgresApi
    extends API
      with DateTimeImplicits
      with ArrayImplicits
      with SprayJsonImplicits {
    implicit val workflowListTypeTypeMapper = createEnumJdbcType("list_type", WorkflowListType)
    implicit val workflowListTypeListTypeMapper = createEnumListJdbcType("list_type", WorkflowListType)
    implicit val workflowListTypeOptionColumnExtensionMethodsBuilder = createEnumOptionColumnExtensionMethodsBuilder(WorkflowListType)

    implicit val workflowListStateTypeMapper = createEnumJdbcType("state", WorkflowListState)
    implicit val workflowListStateListTypeMapper = createEnumListJdbcType("state", WorkflowListState)
    implicit val workflowListStateOptionColumnExtensionMethodsBuilder = createEnumOptionColumnExtensionMethodsBuilder(WorkflowListState)

    implicit val workflowListUseCaseTypeMapper = createEnumJdbcType("use_case", WorkflowListUseCase)
    implicit val workflowListUseCaseListTypeMapper = createEnumListJdbcType("use_case", WorkflowListUseCase)
    implicit val workflowListUseCaseOptionColumnExtensionMethodsBuilder = createEnumOptionColumnExtensionMethodsBuilder(WorkflowListUseCase)

    implicit val workflowListDataSourceTypeMapper = createEnumJdbcType("data_source", WorkflowListDataSource)
    implicit val workflowListDataSourceListTypeMapper = createEnumListJdbcType("data_source", WorkflowListDataSource)
    implicit val workflowListDataSourceOptionColumnExtensionMethodsBuilder = createEnumOptionColumnExtensionMethodsBuilder(WorkflowListDataSource)

    implicit val eventTypeTypeMapper = createEnumJdbcType("event_type", EventType)
    implicit val eventTypeListTypeMapper = createEnumListJdbcType("event_type", EventType)
    implicit val eventTypeOptionColumnExtensionMethodsBuilder = createEnumOptionColumnExtensionMethodsBuilder(EventType)

    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)
  }

}

object MyPostgresProfile extends MyPostgresProfile
