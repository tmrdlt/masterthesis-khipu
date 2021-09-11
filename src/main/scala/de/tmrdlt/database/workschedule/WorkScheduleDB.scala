package de.tmrdlt.database.workschedule

import de.tmrdlt.database.MyDB._
import de.tmrdlt.database.MyPostgresProfile.api._

import scala.concurrent.Future

class WorkScheduleDB {
  def getWorkSchedule: Future[WorkSchedule] =
    db.run(workScheduleQuery.result.head)
}
