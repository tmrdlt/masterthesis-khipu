package de.tmrdlt.database

import de.tmrdlt.database.event.EventDB
import de.tmrdlt.database.user.UserDB
import de.tmrdlt.database.workflowlist.WorkflowListDB
import de.tmrdlt.database.workflowlistresource.WorkflowListResourceDB
import de.tmrdlt.database.workschedule.WorkScheduleDB

class DBs {
  val eventDB = new EventDB()
  val workflowListDB = new WorkflowListDB()
  val workflowListResourceDB = new WorkflowListResourceDB()
  val userDB = new UserDB()
  val workScheduleDB = new WorkScheduleDB()
}
