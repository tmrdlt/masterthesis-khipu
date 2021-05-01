package de.tmrdlt.database.user

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyPostgresProfile.api._
import slick.lifted.{ProvenShape, Rep, Tag}
import slick.sql.SqlProfile.ColumnOption.NotNull

import java.time.LocalDateTime

case class User(id: Long,
                apiId: String,
                username: String,
                createdAt: LocalDateTime,
                updatedAt: LocalDateTime)

class UserTable(tag: Tag) extends BaseTableLong[User](tag, "user") {

  def apiId: Rep[String] = column[String]("api_id", NotNull)

  def username: Rep[String] = column[String]("username", NotNull)

  def createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at", NotNull)

  def updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at", NotNull)

  def * : ProvenShape[User] = (
    id,
    apiId,
    username,
    createdAt,
    updatedAt
  ) <> (User.tupled, User.unapply)
}
