package de.tmrdlt.database

import de.tmrdlt.database.action.EventDB
import de.tmrdlt.database.workflowlist.WorkflowListDB

class DBs {
  val workflowListDB = new WorkflowListDB()
  val actionDB = new EventDB()
}
