package de.tmrdlt.components.workflowlist.id.query

import de.tmrdlt.database.event.{Event, EventDB}
import de.tmrdlt.models.{TemporalQueryResultEntity, WorkflowListEntity}
import de.tmrdlt.services.WorkflowListService
import de.tmrdlt.utils.SimpleNameLogger

import java.time.temporal.ChronoUnit
import java.time.{DayOfWeek, LocalDateTime}
import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.math.Ordered.orderingToOrdered


class WorkflowListIdQueryController(workflowListService: WorkflowListService,
                                    eventDB: EventDB) extends SimpleNameLogger {

  val workingDaysOfWeek: Seq[DayOfWeek] = Seq(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
  val startWorkAtHour: Int = 10
  val stopWorkAtHour: Int = 18
  val assumedDurationForTasksWithoutDuration: Int = 0

  // TODO make recursive for all children
  // Annahmen:
  // 1. Erste Spalte des Boards entspricht open items, letzte Spalte entspricht done items, alle dazwischen sind in progress items
  def getDurationOfAllTasks(workflowListApiId: String): Future[TemporalQueryResultEntity] = {
    val now = LocalDateTime.now()
    for {
      board <- workflowListService.getWorkflowListEntityForId(workflowListApiId)
      events <- eventDB.getEvents
    } yield {
      val open = board.children.head
      val done = board.children.last.children
      val inProgress = board.children.drop(1).dropRight(1)

      val durationOfProject = getDurationOfWorkflowListsInOpen(open) + getDurationOfWorkflowListsInProgress(open, inProgress, events)
      val totalFinishDateByDuration = getFinishDateForStartDateAndDuration(now, durationOfProject)
      val bestExecutionResult = getBestExecutionOrderOfTasks(now, open.children)


      TemporalQueryResultEntity(
        totalDurationMinutes = durationOfProject,
        totalFinishDateByDuration = totalFinishDateByDuration,
        openTasksPredictedFinishDate = bestExecutionResult.calculatedEndDate,
        bestExecutionOrder = bestExecutionResult.executionOrder,
        numberOfFailedDueDates = bestExecutionResult.numberOfDueDatesFailed
      )

    }
  }

  def getDurationOfWorkflowListsInOpen(open: WorkflowListEntity): Long = {
    // TODO take all tasks here. Tasks without duration: Either duration is assumed 0 or duration, one place to get this
    open.children.flatMap(_.temporalResource.flatMap(_.durationInMinutes)).sum
  }

  def getDurationOfWorkflowListsInProgress(open: WorkflowListEntity, inProgress: Seq[WorkflowListEntity], events: Seq[Event]): Long = {
    // D_total = SUM(D_inProgress - T_inProgress)
    // TODO take all tasks here. Tasks without duration: Either duration is assumed 0 or duration, one place to get this
    val allItemsInProgressWithDurations = inProgress.flatMap(_.children).filter(item => item.temporalResource.isDefined && item.temporalResource.get.durationInMinutes.isDefined)

    allItemsInProgressWithDurations.map { itemInProgress =>
      val movedToInProgressDateOption = events.filter(e => e.workflowListApiId == itemInProgress.apiId && e.oldParentApiId.contains(open.apiId)).sortBy(_.date).headOption.map(_.date)
      val createdAtInProgressDateOption = events.filter(e => e.workflowListApiId == itemInProgress.apiId && e.parentApiId.contains(open.apiId)).sortBy(_.date).headOption.map(_.date)
      val now = LocalDateTime.now()

      // Time in inProgress: Time passed since moved to inProgress OR time passed sinced created in inProgress
      val timeInProgress = (movedToInProgressDateOption, createdAtInProgressDateOption) match {
        case (Some(fromDate), _) => ChronoUnit.MINUTES.between(fromDate, now)
        case (_, Some(fromDate)) => ChronoUnit.MINUTES.between(fromDate, now)
        case _ => 0
      }

      // When longer in progress than duration: Task is finished, predicted duration = 0
      val durationFromNow = itemInProgress.temporalResource.get.durationInMinutes.get - timeInProgress
      if (durationFromNow > 0) {
        durationFromNow
      } else {
        0
      }
    }.sum
  }

  def getFinishDateForStartDateAndDuration(startDate: LocalDateTime, durationInMinutes: Long): LocalDateTime = {

    @tailrec
    def getFinishDateRecursive(startDate: LocalDateTime, durationInMinutes: Long): LocalDateTime = {
      val stopDate = startDate.withHour(stopWorkAtHour).withMinute(0).withSecond(0)
      val potentialStartDate = startDate.withHour(startWorkAtHour).withMinute(0).withSecond(0)
      //log.info("startDate: " + startDate.toString + " stopDate: " + stopDate.toString)
      val startWorkAt = {
        // Saturday: Start monday morning
        if (startDate.getDayOfWeek.getValue == 6) { // TODO make adjustable. e.g. if getDayOfWeek is inside workingDaysOfWeek...
          startDate.plusDays(2).withHour(startWorkAtHour).withMinute(0).withSecond(0)
        }
        // Sunday: Start monday morning
        else if (startDate.getDayOfWeek.getValue == 7) {
          startDate.plusDays(1).withHour(startWorkAtHour).withMinute(0).withSecond(0)
        }
        // Friday and after stopWorkAtHour: Start monday morning
        else if (startDate.getDayOfWeek.getValue == 5 && startDate >= stopDate) {
          startDate.plusDays(3).withHour(startWorkAtHour).withMinute(0).withSecond(0)
        }
        // After stopWorkAtHour: Start tomorrow morning
        else if (startDate >= stopDate) {
          startDate.plusDays(1).withHour(startWorkAtHour).withMinute(0).withSecond(0)
        }
        // Before startWorkAtHour: Start today at startWorkAtHour
        else if (startDate < potentialStartDate) {
          potentialStartDate
        }
        // Start today
        else {
          startDate
        }
      }

      val stopWorkAt = startWorkAt.withHour(stopWorkAtHour).withMinute(0).withSecond(0)

      val minutesThatCanBeWorkedToday = ChronoUnit.MINUTES.between(startWorkAt, stopWorkAt)
      val actualMinutesWorked =
        if (durationInMinutes < minutesThatCanBeWorkedToday) durationInMinutes
        else minutesThatCanBeWorkedToday
      val minutesRemaining = durationInMinutes - actualMinutesWorked

      //log.info("startWorkAt: " + startWorkAt.toString + " stopWorkAt: " + stopWorkAt.toString + " Minutes that can be worked today: " + minutesThatCanBeWorkedToday.toString + " Actual minutes worked: " + actualMinutesWorked.toString + " Minutes remaining: " + minutesRemaining.toString)
      if (minutesRemaining > 0) {
        getFinishDateRecursive(startWorkAt.plusMinutes(actualMinutesWorked), minutesRemaining)
      } else {
        startWorkAt.plusMinutes(actualMinutesWorked)
      }
    }

    val startDateRounded = startDate.withSecond(0).withNano(0)
    // TODO MAYBE show if possible to finish today: return finish date as LocalDateTime
    //if (startDateRounded.plusMinutes(durationInMinutes).toLocalDate == startDateRounded.toLocalDate) {
    //  startDateRounded.plusMinutes(durationInMinutes)
    //} else {
    getFinishDateRecursive(startDateRounded, durationInMinutes)
    //}
  }


  def getBestExecutionOrderOfTasks(now: LocalDateTime, openTasks: Seq[WorkflowListEntity]): WorkflowListExecutionTreeEvaluation = {
    // Only schedule tasks with a duration defined TODO take all tasks here. Tasks without duration: Either duration is assumed 0 or duration, one place to get this
    val relevantTasks = openTasks.filter(item => item.temporalResource.isDefined && item.temporalResource.get.durationInMinutes.isDefined)

    relevantTasks.permutations.map { tasksPermutation =>
      var startDate = now
      var endDate = now
      var numberOfDueDatesFailed = 0
      // TODO make recursive function
      tasksPermutation.foreach { task =>
        startDate = task.getStartDate match {
          case Some(date) => Seq(date, startDate).max
          case _ => endDate
        }
        endDate = getFinishDateForStartDateAndDuration(startDate, task.temporalResource.get.durationInMinutes.get)
        //log.info("endDate" + endDate)
        if (task.getEndDate.exists(_ < endDate)) {
          numberOfDueDatesFailed = numberOfDueDatesFailed + 1
        }
      }
      WorkflowListExecutionTreeEvaluation(tasksPermutation.map(_.title), endDate, numberOfDueDatesFailed)
    }.toSeq.min
  }
}

// TODO move elsewhere
case class WorkflowListExecutionTreeEvaluation(executionOrder: Seq[String],
                                               calculatedEndDate: LocalDateTime,
                                               numberOfDueDatesFailed: Int) // TODO To show in frontend make object, which contains wl ID and projected due date

object WorkflowListExecutionTreeEvaluation {
  // Note that because `Ordering[A]` is not contravariant, the declaration
  // must be type-parametrized in the event that you want the implicit
  // ordering to apply to subclasses of `Employee`.
  implicit def ordering[A <: WorkflowListExecutionTreeEvaluation]: Ordering[A] =
    Ordering.by(t => (t.numberOfDueDatesFailed, t.calculatedEndDate))
}

