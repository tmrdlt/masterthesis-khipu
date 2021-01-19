package de.tmrdlt.database.nestingrelation

import de.tmrdlt.database.MyDB._
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}
import de.tmrdlt.database.MyPostgresProfile.api._


class NestingRelationDB
  extends SimpleNameLogger
    with OptionExtensions {

  private def insertNestingRelationQuery(nestingRelation: NestingRelation) =
    (nestingRelationQuery returning nestingRelationQuery) += nestingRelation

}