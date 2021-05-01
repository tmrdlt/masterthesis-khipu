package de.tmrdlt.models

import de.tmrdlt.database.user.User
import spray.json.RootJsonFormat

case class CreateUserEntity(username: String)

trait UserJsonSupport extends JsonSupport {
  implicit val createUserEntityFormat: RootJsonFormat[CreateUserEntity] = jsonFormat1(CreateUserEntity)
  implicit val userFormat: RootJsonFormat[User] = jsonFormat5(User)

}
