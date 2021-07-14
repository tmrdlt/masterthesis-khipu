package de.tmrdlt.components.workflowlist.id.query

import de.tmrdlt.services.WorkflowListService

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class WorkflowListIdQueryController(workflowListService: WorkflowListService) {

  def temporalPredictionForBoard(workflowListApiId: String): Future[LocalDateTime] = {
    for {
      boardWithChilds <- workflowListService.getWorkflowListEntityForId(workflowListApiId)
    } yield {
      boardWithChilds.temporalResource.getOrElse(throw new Exception("no temp res")).endDate.getOrElse(LocalDateTime.now())
    }
  }

}
