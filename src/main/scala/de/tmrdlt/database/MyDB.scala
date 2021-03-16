package de.tmrdlt.database

import de.tmrdlt.database.event.EventTable
import de.tmrdlt.database.temporalcontraint.TemporalConstraintTable
import de.tmrdlt.database.workflowlist.WorkflowListTable
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery


object MyDB {

  private val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("workflow.postgres")

  val db: JdbcProfile#Backend#Database = dbConfig.db

  val workflowListQuery = TableQuery[WorkflowListTable]
  val eventQuery = TableQuery[EventTable]
  val temporalConstraintQuery = TableQuery[TemporalConstraintTable]
}
