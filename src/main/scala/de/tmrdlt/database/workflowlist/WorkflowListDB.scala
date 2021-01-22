package de.tmrdlt.database.workflowlist

import de.tmrdlt.database.MyDB._
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.CreateWorkflowListEntity
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}

import java.time.LocalDateTime
import scala.concurrent.Future


class WorkflowListDB
  extends SimpleNameLogger
    with OptionExtensions {


  def insertWorkflowListQuery(createWorkflowListEntity: CreateWorkflowListEntity): Future[WorkflowList] = {
    db.run(
      (workflowListQuery returning workflowListQuery) += {
        val now = LocalDateTime.now()
        WorkflowList(
          id = 0L,
          uuid = java.util.UUID.randomUUID,
          title = createWorkflowListEntity.title,
          description = createWorkflowListEntity.description,
          parentId = None, // TODO pass as parameter
          createdAt = now,
          updatedAt = now
        )
      }
    )
  }
}
