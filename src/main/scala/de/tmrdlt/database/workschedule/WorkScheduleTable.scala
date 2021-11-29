package de.tmrdlt.database.workschedule

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.JsonSupport
import slick.lifted.{ProvenShape, Rep}
import slick.sql.SqlProfile.ColumnOption.NotNull
import spray.json.RootJsonFormat

import java.time.{DayOfWeek, LocalDateTime}

trait WorkScheduleJsonSupport extends JsonSupport {
  implicit val workScheduleFormat: RootJsonFormat[WorkSchedule] = jsonFormat4(WorkSchedule)
}

/**
 * Database representation of the work schedule.
 *
 * @param startWorkAtHour     Hour in Int at which working starts on a work day
 * @param stopWorkAtHour      Hour in Int at which working stops on a work day
 * @param workingDaysOfWeek   Worked on days of the week
 * @param schedulingStartDate Optionally configure date time at which the scheduling algorithm should start calculating.
 */
case class WorkSchedule(startWorkAtHour: Int,
                        stopWorkAtHour: Int,
                        workingDaysOfWeek: List[DayOfWeek],
                        schedulingStartDate: Option[LocalDateTime])

class WorkScheduleTable(tag: Tag)
  extends BaseTableLong[WorkSchedule](tag, "work_schedule") {

  def startWorkAtHour: Rep[Int] = column[Int]("start_work_at_hour", NotNull)

  def stopWorkAtHour: Rep[Int] = column[Int]("stop_work_at_hour", NotNull)

  def workingDaysOfWeek: Rep[List[String]] = column[List[String]]("working_days_of_week", NotNull)

  def schedulingStartDate: Rep[Option[LocalDateTime]] = column[Option[LocalDateTime]]("scheduling_start_date", NotNull)

  def * : ProvenShape[WorkSchedule] = (
    startWorkAtHour,
    stopWorkAtHour,
    workingDaysOfWeek,
    schedulingStartDate,
  ) <> (intoWorkSchedule, fromWorkSchedule)

  private def intoWorkSchedule(pair: (Int, Int, List[String], Option[LocalDateTime])): WorkSchedule = {
    pair match {
      case (startWorkAtHour, stopWorkAtHour, workingDays, schedulingStartDate) =>
        WorkSchedule(startWorkAtHour, stopWorkAtHour, workingDays.map(str => DayOfWeek.valueOf(str)), schedulingStartDate)
    }
  }

  private def fromWorkSchedule(workSchedule: WorkSchedule): Option[(Int, Int, List[String], Option[LocalDateTime])] =
    Some((
      workSchedule.startWorkAtHour,
      workSchedule.stopWorkAtHour,
      workSchedule.workingDaysOfWeek.map(_.toString),
      workSchedule.schedulingStartDate
    ))
}
