package de.tmrdlt.database.user

import de.tmrdlt.database.MyDB._
import de.tmrdlt.database.MyPostgresProfile.api._

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserDB {

  def getUsers: Future[Seq[User]] = {
    db.run(userQuery.result)
  }

  def getActiveUser(userApiId: String): Future[Option[User]] =
    db.run(userQuery.filter(_.isActive).filter(_.apiId === userApiId).result.headOption)

  def getActiveUserByUserNameSqlAction(username: String): DBIOAction[User, NoStream, Effect.Read] =
    userQuery.filter(_.isActive).filter(_.username === username).result.headOption map {
      case Some(user) => user
      case _ => throw new Exception(s"No active user for username $username found")
    }

  def createUser(username: String): Future[User] = {
    val now = LocalDateTime.now()
    val query = (userQuery returning userQuery) += {
      User(
        id = 0L,
        apiId = java.util.UUID.randomUUID.toString,
        username = username,
        createdAt = now,
        updatedAt = now
      )
    }
    db.run(query)
  }

}
