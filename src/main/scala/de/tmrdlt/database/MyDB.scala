package de.tmrdlt.database

import de.tmrdlt.database.event.EventTable
import de.tmrdlt.database.user.UserTable
import de.tmrdlt.database.workflowlist.WorkflowListTable
import de.tmrdlt.database.workflowlistresource.{NumericResourceTable, TemporalResourceTable, TextualResourceTable, UserResourceTable}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery


object MyDB {

  private val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("workflow.postgres")

  val db: JdbcProfile#Backend#Database = dbConfig.db

  val workflowListQuery = TableQuery[WorkflowListTable]
  val eventQuery = TableQuery[EventTable]
  val userQuery = TableQuery[UserTable]
  val temporalResourceQuery = TableQuery[TemporalResourceTable]
  val numericResourceQuery = TableQuery[NumericResourceTable]
  val textualResourceQuery = TableQuery[TextualResourceTable]
  val userResourceQuery = TableQuery[UserResourceTable]

}
