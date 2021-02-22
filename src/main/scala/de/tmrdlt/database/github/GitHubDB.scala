package de.tmrdlt.database.github

import de.tmrdlt.database.MyDB.{db, gitHubEventQuery}
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.utils.{OptionExtensions, SimpleNameLogger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GitHubDB
  extends SimpleNameLogger
    with OptionExtensions {

  def insertGitHubEvents(gitHubEvents: Seq[GitHubEventDBEntity]): Future[Int] =
    db.run(gitHubEventQuery returning gitHubEventQuery ++= gitHubEvents).map(_.length)

}
