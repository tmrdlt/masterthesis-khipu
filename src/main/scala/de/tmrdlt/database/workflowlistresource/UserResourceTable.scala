package de.tmrdlt.database.workflowlistresource

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyDB.{userQuery, workflowListQuery}
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.database.user.{User, UserTable}
import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListTable}
import de.tmrdlt.models.UserResourceEntity
import slick.ast.ColumnOption.Unique
import slick.lifted.{ForeignKeyQuery, ProvenShape, Rep}
import slick.sql.SqlProfile.ColumnOption.{NotNull, Nullable}

import java.time.LocalDateTime

case class UserResource(id: Long,
                        workflowListId: Long,
                        userId: Option[Long],
                        createdAt: LocalDateTime,
                        updatedAt: LocalDateTime) {

  def toUserResourceEntity(username: Option[String]): UserResourceEntity =
    UserResourceEntity(username = username)
}

class UserResourceTable(tag: Tag)
  extends BaseTableLong[UserResource](tag, "user_resource") {

  def workflowListId: Rep[Long] = column[Long]("workflow_list_id", NotNull, Unique)

  def userId: Rep[Option[Long]] = column[Option[Long]]("user_id", Nullable)

  def createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at", NotNull)

  def updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at", NotNull)

  def workflowListForeignKey: ForeignKeyQuery[WorkflowListTable, WorkflowList] =
    foreignKey("workflow_list_fk", workflowListId, workflowListQuery)(_.id, onDelete = ForeignKeyAction.Cascade)

  def userForeignKey: ForeignKeyQuery[UserTable, User] =
    foreignKey("user_fk", userId, userQuery)(_.id.?, onDelete = ForeignKeyAction.Cascade)

  def * : ProvenShape[UserResource] = (
    id,
    workflowListId,
    userId,
    createdAt,
    updatedAt
  ) <> (UserResource.tupled, UserResource.unapply)
}
