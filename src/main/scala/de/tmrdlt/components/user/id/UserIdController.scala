package de.tmrdlt.components.user.id

import de.tmrdlt.database.user.UserDB
import de.tmrdlt.models.UserEntity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserIdController(userDB: UserDB) {

  def getUser(userApiId: String): Future[UserEntity] =
    userDB.getActiveUser(userApiId).map {
      case Some(user) => user.toUserEntity
      case _ => throw new Exception(s"No active user for userApiId $userApiId found")
    }
}
