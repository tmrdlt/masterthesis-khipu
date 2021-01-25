package de.tmrdlt.components.workflowlist.id.convert

import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.models.UsageType.UsageType

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkflowListIdConvertController(workflowListDB: WorkflowListDB) {

  def convertWorkflowList(workfLowListId: Long, convertWorkflowListTo: UsageType): Future[Int] = {

      // TODO WIP
      // Board -> Board: Nothing
      // Board -> List: Delete All tasks get merged
      // Board -> Task:

      // Task -> List: Add element that has Task as parent
      // List -> Board: Add element to every element that has list as parent
      // Task -> Board: Add
      Future.successful(1)
  }
}