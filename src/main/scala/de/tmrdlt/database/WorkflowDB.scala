package de.tmrdlt.database

import de.tmrdlt.database.tables.BoardTable
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery


object WorkflowDB {

  private val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("workflow.postgres")

  val db: JdbcProfile#Backend#Database = dbConfig.db


  val boardQuery = TableQuery[BoardTable]
}

