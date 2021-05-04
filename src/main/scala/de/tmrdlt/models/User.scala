package de.tmrdlt.models

import spray.json.RootJsonFormat

import java.time.LocalDateTime

case class UserEntity(apiId: String,
                      username: String,
                      createdAt: LocalDateTime,
                      updatedAt: LocalDateTime)

case class CreateUserEntity(username: String)

trait UserJsonSupport extends JsonSupport {
  implicit val createUserEntityFormat: RootJsonFormat[CreateUserEntity] = jsonFormat1(CreateUserEntity)
  implicit val userEntityFormat: RootJsonFormat[UserEntity] = jsonFormat4(UserEntity)
}
