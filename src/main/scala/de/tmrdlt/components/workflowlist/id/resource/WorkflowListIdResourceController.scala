package de.tmrdlt.components.workflowlist.id.resource

import de.tmrdlt.database.user.UserDB
import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.database.workflowlistresource.WorkflowListResourceDB
import de.tmrdlt.models.WorkflowListResourceEntity

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class WorkflowListIdResourceController(workflowListDB: WorkflowListDB,
                                       workflowListResourceDB: WorkflowListResourceDB,
                                       userDB: UserDB) {

  def updateWorkflowListResource(workflowListApiId: String,
                                 entity: WorkflowListResourceEntity): Future[Int] = {
    val now = LocalDateTime.now()
    for {
      workflowList <- workflowListDB.getWorkflowList(workflowListApiId)
      inserted <- workflowListResourceDB.insertOrUpdateWorkflowListResource(now, workflowList.id, entity, userDB)
    } yield {
      inserted
    }
  }
}
