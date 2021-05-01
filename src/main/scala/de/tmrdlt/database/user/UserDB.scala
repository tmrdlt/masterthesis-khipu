package de.tmrdlt.database.user

import de.tmrdlt.database.MyDB._
import de.tmrdlt.database.MyPostgresProfile.api._

import java.time.LocalDateTime
import scala.concurrent.Future

class UserDB {

  def getUsers: Future[Seq[User]] = {
    db.run(userQuery.result)
  }

  def createUser(username: String): Future[User] = {
    val now = LocalDateTime.now()
    val query = (userQuery returning userQuery) += {
      User(
        id = 0L,
        apiId = java.util.UUID.randomUUID.toString,
        userName = username,
        createdAt = now,
        updatedAt = now
      )
    }
    db.run(query)
  }

}
