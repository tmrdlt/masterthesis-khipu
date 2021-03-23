package de.tmrdlt.components.workflowlist.id.tempconstraint

import de.tmrdlt.database.temporalcontraint.{TemporalConstraint, TemporalConstraintDB}
import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.models.TemporalConstraintEntity

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class WorkflowListIdTempConstraintController(workflowListDB: WorkflowListDB,
                                             temporalConstraintDB: TemporalConstraintDB) {

  def createOrUpdateTemporalConstraint(workflowListApiId: String,
                                       entity: TemporalConstraintEntity): Future[Int] = {
    val now = LocalDateTime.now()
    for {
      workflowList <- workflowListDB.getWorkflowList(workflowListApiId)
      connectedWorkflowListOption <- entity.connectedWorkflowListApiId match {
        case Some(apiId) => workflowListDB.getWorkflowList(apiId).map(wl => Some(wl))
        case _ => Future.successful(None)
      }
      temporalConstraintOption <- temporalConstraintDB.getTemporalConstraint(workflowList.id)
      upserted <- temporalConstraintDB.insertOrUpdateTemporalConstraint(
        TemporalConstraint(
          id = temporalConstraintOption.map(_.id).getOrElse(0L),
          workflowListId = workflowList.id,
          temporalConstraintType = entity.temporalConstraintType,
          dueDate = entity.dueDate,
          connectedWorkflowListId = connectedWorkflowListOption.map(_.id),
          createdAt = temporalConstraintOption.map(_.createdAt).getOrElse(now),
          updatedAt = now
        )
      )
    } yield {
      upserted
    }
  }

}
