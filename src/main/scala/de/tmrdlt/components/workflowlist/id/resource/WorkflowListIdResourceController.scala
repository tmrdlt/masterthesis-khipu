package de.tmrdlt.components.workflowlist.id.resource

import de.tmrdlt.database.user.UserDB
import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.database.workflowlistresource.WorkflowListResourceDB
import de.tmrdlt.models.WorkflowListResourceEntity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class WorkflowListIdResourceController(workflowListDB: WorkflowListDB,
                                       workflowListResourceDB: WorkflowListResourceDB,
                                       userDB: UserDB) {

  def updateWorkflowListResource(workflowListApiId: String,
                                 entity: WorkflowListResourceEntity,
                                 userApiId: String): Future[Int] = {
    for {
      workflowList <- workflowListDB.getWorkflowList(workflowListApiId)
      inserted <- workflowListResourceDB.insertOrUpdateWorkflowListResource(workflowList.id, entity, userApiId, userDB)
    } yield {
      inserted
    }
  }
}
