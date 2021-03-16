package de.tmrdlt.database

import de.tmrdlt.database.event.EventDB
import de.tmrdlt.database.temporalcontraint.TemporalConstraintDB
import de.tmrdlt.database.workflowlist.WorkflowListDB

class DBs {
  val workflowListDB = new WorkflowListDB()
  val actionDB = new EventDB()
  val temporalConstraintDB = new TemporalConstraintDB()
}
