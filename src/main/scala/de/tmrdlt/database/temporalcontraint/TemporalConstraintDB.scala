package de.tmrdlt.database.temporalcontraint

import de.tmrdlt.database.MyDB._
import de.tmrdlt.database.MyPostgresProfile.api._

import scala.concurrent.Future

class TemporalConstraintDB {

  def getTemporalConstraints: Future[Seq[TemporalConstraint]] =
    db.run(temporalConstraintQuery.result)

}
