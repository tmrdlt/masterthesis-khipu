package de.tmrdlt.database

import com.github.tminglei.slickpg.{ExPostgresProfile, PgDate2Support}
import de.tmrdlt.utils.SimpleNameLogger
import slick.util.SlickLogger

trait WorkflowPostgresProfile
  extends SimpleNameLogger // Must be first so log is already there for ExPostgresProfile
    with ExPostgresProfile
    with PgDate2Support {

  override val api: WorkflowPostgresApi.type = WorkflowPostgresApi
  override lazy val logger: SlickLogger = new SlickLogger(log)

  object WorkflowPostgresApi
    extends API
      with DateTimeImplicits

}

object WorkflowPostgresProfile extends WorkflowPostgresProfile
