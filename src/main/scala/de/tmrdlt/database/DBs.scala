package de.tmrdlt.database

import de.tmrdlt.database.trello.TrelloDB
import de.tmrdlt.database.workflowlist.WorkflowListDB

class DBs {
  val workflowListDB = new WorkflowListDB()
  val trelloDB = new TrelloDB()
}
