package de.tmrdlt.database.workschedule

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.JsonSupport
import slick.lifted.{ProvenShape, Rep}
import slick.sql.SqlProfile.ColumnOption.NotNull
import spray.json.RootJsonFormat

import java.time.DayOfWeek

trait WorkScheduleJsonSupport extends JsonSupport {
  implicit val workScheduleFormat: RootJsonFormat[WorkSchedule] = jsonFormat3(WorkSchedule)
}

case class WorkSchedule(startWorkAtHour: Int,
                        stopWorkAtHour: Int,
                        workingDaysOfWeek: List[DayOfWeek])

class WorkScheduleTable(tag: Tag)
  extends BaseTableLong[WorkSchedule](tag, "work_schedule") {

  def startWorkAtHour: Rep[Int] = column[Int]("start_work_at_hour", NotNull)

  def stopWorkAtHour: Rep[Int] = column[Int]("stop_work_at_hour", NotNull)

  def workingDaysOfWeek: Rep[List[String]] = column[List[String]]("working_days_of_week", NotNull)

  def * : ProvenShape[WorkSchedule] = (
    startWorkAtHour,
    stopWorkAtHour,
    workingDaysOfWeek
  ) <> (intoWorkSchedule, fromWorkSchedule)

  private def intoWorkSchedule(pair: (Int, Int, List[String])): WorkSchedule = {
    pair match {
      case (startWorkAtHour, stopWorkAtHour, workingDays) =>
        WorkSchedule(startWorkAtHour, stopWorkAtHour, workingDays.map(str => DayOfWeek.valueOf(str)))
    }
  }

  private def fromWorkSchedule(workSchedule: WorkSchedule): Option[(Int, Int, List[String])] =
    Some((
      workSchedule.startWorkAtHour,
      workSchedule.stopWorkAtHour,
      workSchedule.workingDaysOfWeek.map(_.toString),
    ))
}
