package de.tmrdlt.database.temporalcontraint

import de.tmrdlt.database.MyDB._
import de.tmrdlt.database.MyPostgresProfile.api._

import scala.concurrent.Future

class TemporalConstraintDB {

  def getTemporalConstraints: Future[Seq[TemporalConstraint]] =
    db.run(temporalConstraintQuery.result)

  def getTemporalConstraints(workflowListIds: Seq[Long]): Future[Seq[TemporalConstraint]] =
    db.run(temporalConstraintQuery.filter(_.workflowListId inSet workflowListIds).result)

  def getTemporalConstraint(workflowListId: Long): Future[Option[TemporalConstraint]] =
    db.run(temporalConstraintQuery.filter(_.workflowListId === workflowListId).result.headOption)

  def insertOrUpdateTemporalConstraint(temporalConstraint: TemporalConstraint): Future[Int] = {
    val query = temporalConstraintQuery
      .insertOrUpdate(temporalConstraint)
    db.run(query)
  }

}
