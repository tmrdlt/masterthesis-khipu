package de.tmrdlt.components.workflowlist.id.resource.temporal

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.database.workflowlistresource.{TemporalResource, WorkflowListResourceDB}
import de.tmrdlt.models.TemporalResourceEntity

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class WorkflowListIdTempResourceController(workflowListDB: WorkflowListDB,
                                           workflowListResourceDB: WorkflowListResourceDB) {

  def createOrUpdateTemporalResource(workflowListApiId: String,
                                     entity: TemporalResourceEntity): Future[Int] = {
    val now = LocalDateTime.now()
    for {
      workflowList <- workflowListDB.getWorkflowList(workflowListApiId)
      connectedWorkflowListOption <- entity.connectedWorkflowListApiId match {
        case Some(apiId) => workflowListDB.getWorkflowList(apiId).map(wl => Some(wl))
        case _ => Future.successful(None)
      }
      temporalResourceOption <- workflowListResourceDB.getTemporalResource(workflowList.id)
      upserted <- workflowListResourceDB.insertOrUpdateWorkflowListResource(
        TemporalResource(
          id = temporalResourceOption.map(_.id).getOrElse(0L),
          workflowListId = workflowList.id,
          startDate = entity.startDate,
          endDate = entity.endDate,
          durationInMinutes = entity.durationInMinutes,
          connectedWorkflowListId = connectedWorkflowListOption.map(_.id),
          createdAt = temporalResourceOption.map(_.createdAt).getOrElse(now),
          updatedAt = now
        )
      )
    } yield {
      upserted
    }
  }

}
