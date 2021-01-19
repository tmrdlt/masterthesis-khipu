package de.tmrdlt.database.workflowlist

import de.tmrdlt.database.MyDB._
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}
import de.tmrdlt.database.MyPostgresProfile.api._


class WorkflowListDB
  extends SimpleNameLogger
    with OptionExtensions {

  private def insertWorkflowListQuery(workflowList: WorkflowList) =
    (workflowListQuery returning workflowListQuery) += workflowList

}
