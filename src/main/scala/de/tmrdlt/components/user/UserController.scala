package de.tmrdlt.components.user

import de.tmrdlt.database.user.{User, UserDB}
import de.tmrdlt.models.CreateUserEntity

import scala.concurrent.Future

class UserController(userDB: UserDB) {

  def getUsers: Future[Seq[User]] =
    userDB.getUsers

  def createUser(createUserEntity: CreateUserEntity): Future[User] =
    userDB.createUser(createUserEntity.username)


}
