package de.tmrdlt.database

import de.tmrdlt.database.MyPostgresProfile.api._

abstract class BaseTableLong[T](tag: Tag,
                                name: String)
  extends Table[T](
    _tableTag = tag,
    _schemaName = Some("workflow"),
    _tableName = name
  ) {

  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
}
