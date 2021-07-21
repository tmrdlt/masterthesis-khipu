package de.tmrdlt.services

import de.tmrdlt.database.DBs

class Services(dbs: DBs) {

  val workflowListService = new WorkflowListService(dbs.workflowListDB, dbs.workflowListResourceDB, dbs.userDB)
  val workScheduleService = new WorkScheduleService
}
