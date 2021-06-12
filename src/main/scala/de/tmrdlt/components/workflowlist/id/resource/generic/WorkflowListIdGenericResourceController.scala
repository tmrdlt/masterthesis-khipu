package de.tmrdlt.components.workflowlist.id.resource.generic

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.database.workflowlistresource.{GenericResource, WorkflowListResourceDB}
import de.tmrdlt.models.GenericResourceEntity

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class WorkflowListIdGenericResourceController(workflowListDB: WorkflowListDB,
                                              workflowListResourceDB: WorkflowListResourceDB) {

  def createGenericResource(workflowListApiId: String,
                            entity: GenericResourceEntity): Future[Int] = {
    val now = LocalDateTime.now()
    for {
      workflowList <- workflowListDB.getWorkflowList(workflowListApiId)
      genericResourceOption <- workflowListResourceDB.getGenericResource(workflowList.id, entity.label)
      inserted <- workflowListResourceDB.insertOrUpdateWorkflowListResource(
        GenericResource(
          id = genericResourceOption.map(_.id).getOrElse(0L),
          workflowListId = workflowList.id,
          label = entity.label,
          value = entity.value,
          createdAt = genericResourceOption.map(_.createdAt).getOrElse(now),
          updatedAt = now
        )
      )
    } yield {
      inserted
    }
  }

  def deleteGenericResource(workflowListApiId: String,
                            entity: GenericResourceEntity): Future[Int] = {
    for {
      workflowList <- workflowListDB.getWorkflowList(workflowListApiId)
      genericResourceOption <- workflowListResourceDB.getGenericResource(workflowList.id, entity.label)
      deleted <- workflowListResourceDB.deleteGenericResource(workflowList.id, entity.label)
    } yield {
      deleted
    }
  }
}
