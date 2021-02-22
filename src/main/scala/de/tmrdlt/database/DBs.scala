package de.tmrdlt.database

import de.tmrdlt.database.action.ActionDB
import de.tmrdlt.database.github.GitHubDB
import de.tmrdlt.database.trello.TrelloDB
import de.tmrdlt.database.workflowlist.WorkflowListDB

class DBs {
  val workflowListDB = new WorkflowListDB()
  val actionDB = new ActionDB()
  val trelloDB = new TrelloDB()
  val gitHubDB = new GitHubDB()
}
