package de.tmrdlt.components.user.id

import de.tmrdlt.database.user.{User, UserDB}

import scala.concurrent.Future

class UserIdController(userDB: UserDB) {

  def getUser(userApiId: String): Future[User] =
    userDB.getActiveUser(userApiId)

}
