package de.tmrdlt.components.workflowlist.id.resource.generic

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.database.workflowlistresource.{GenericResource, WorkflowListResourceDB}
import de.tmrdlt.models.GenericResourceEntity

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class WorkflowListIdGenericResourceController(workflowListDB: WorkflowListDB,
                                              workflowListResourceDB: WorkflowListResourceDB) {

  def updateGenericResources(workflowListApiId: String,
                             genericResourceEntities: Seq[GenericResourceEntity]): Future[Int] = {
    val now = LocalDateTime.now()
    for {
      workflowList <- workflowListDB.getWorkflowList(workflowListApiId)
      existingGenericResources <- Future.sequence(genericResourceEntities.map(gre => workflowListResourceDB.getGenericResource(workflowList.id, gre.label)))
      inserted <- workflowListResourceDB.updateGenericResources(
        workflowList.id,
        genericResourceEntities.map { gre =>
          val existingGenericResource = existingGenericResources.find(_.map(_.label).contains(gre.label)).flatten
          GenericResource(
            id = existingGenericResource.map(_.id).getOrElse(0L),
            workflowListId = workflowList.id,
            label = gre.label,
            value = gre.value,
            createdAt = existingGenericResource.map(_.createdAt).getOrElse(now),
            updatedAt = now
          )
        }
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
