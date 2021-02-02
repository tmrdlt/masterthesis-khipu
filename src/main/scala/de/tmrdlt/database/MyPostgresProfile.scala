package de.tmrdlt.database

import com.github.tminglei.slickpg.{ExPostgresProfile, PgDate2Support, PgEnumSupport}
import de.tmrdlt.models.UsageType
import de.tmrdlt.utils.SimpleNameLogger
import slick.util.SlickLogger

trait MyPostgresProfile
  extends SimpleNameLogger // Must be first so log is already there for ExPostgresProfile
    with ExPostgresProfile
    with PgDate2Support
    with PgEnumSupport {

  override val api: MyPostgresApi.type = MyPostgresApi
  override lazy val logger: SlickLogger = new SlickLogger(log)

  object MyPostgresApi
    extends API
      with DateTimeImplicits {
    implicit val usageTypeTypeMapper = createEnumJdbcType("usage_type", UsageType)
    implicit val usageTypeListTypeMapper = createEnumListJdbcType("usage_type", UsageType)
    implicit val usageTypeOptionColumnExtensionMethodsBuilder = createEnumOptionColumnExtensionMethodsBuilder(UsageType)
  }

}

object MyPostgresProfile extends MyPostgresProfile
