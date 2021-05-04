package de.tmrdlt.components.user

import de.tmrdlt.database.user.UserDB
import de.tmrdlt.models.{CreateUserEntity, UserEntity}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserController(userDB: UserDB) {

  def getUsers: Future[Seq[UserEntity]] =
    userDB.getUsers.map(_.map(_.toUserEntity))

  def createUser(createUserEntity: CreateUserEntity): Future[UserEntity] =
    userDB.createUser(createUserEntity.username).map(_.toUserEntity)
}
