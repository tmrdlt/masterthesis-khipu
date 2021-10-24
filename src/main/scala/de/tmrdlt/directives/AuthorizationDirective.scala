package de.tmrdlt.directives

import akka.http.scaladsl.server.Directives.{onSuccess, optionalHeaderValueByName, provide, reject}
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive1}
import de.tmrdlt.database.user.UserDB

class AuthorizationDirective(userDB: UserDB) {

  def authorizeUser: Directive1[String] = {
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(authHeader) =>
        onSuccess(userDB.getActiveUser(authHeader)).flatMap {
          case Some(user) => provide(user.apiId)
          case None => reject(AuthorizationFailedRejection)
        }
      case None => reject(AuthorizationFailedRejection)
    }
  }
}
