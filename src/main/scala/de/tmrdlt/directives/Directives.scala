package de.tmrdlt.directives

import de.tmrdlt.database.DBs

class Directives(dbs: DBs) {

  val authorizationDirective = new AuthorizationDirective(dbs.userDB)

}
