package de.tmrdlt.database.tables


import de.tmrdlt.database.WorkflowPostgresProfile.api._

/** Abstraction over our database tables for tables with ids of type Long
  *
  * @constructor Takes the tag and name of the table in the database and sets a default schema
  * @param tag  marks a specific row represented by an AbstractTable instance
  * @param name of the table in the database
  */
abstract class BaseTableLong[T](tag: Tag, name: String)
  extends Table[T](tag, Some("workflow"), name) {
  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
}
