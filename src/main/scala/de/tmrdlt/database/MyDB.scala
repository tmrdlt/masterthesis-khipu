package de.tmrdlt.database

import de.tmrdlt.database.trello.TrelloActionTable
import de.tmrdlt.database.workflowlist.WorkflowListTable
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery


object MyDB {

  private val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("workflow.postgres")

  val db: JdbcProfile#Backend#Database = dbConfig.db

  val workflowListQuery = TableQuery[WorkflowListTable]
  val trelloActionQuery = TableQuery[TrelloActionTable]

}

