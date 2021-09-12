package de.tmrdlt.services

import de.tmrdlt.database.workschedule.WorkSchedule
import de.tmrdlt.models.{TaskPlanningSolution, WorkflowListTemporal, WorkflowListType}
import de.tmrdlt.services.scheduling.domain.{Assignee, Task, TaskSchedule}
import de.tmrdlt.utils.{SimpleNameLogger, WorkScheduleUtil}
import org.optaplanner.core.api.solver.{SolverJob, SolverManager}
import org.optaplanner.core.config.solver.{SolverConfig, SolverManagerConfig}

import java.time.LocalDateTime
import java.util.UUID
import scala.jdk.CollectionConverters.IterableHasAsScala
import scala.math.Ordering.Implicits.infixOrderingOps

class SchedulingService extends SimpleNameLogger {

  def scheduleTasks(now: LocalDateTime,
                    workSchedule: WorkSchedule,
                    workflowLists: Seq[WorkflowListTemporal]): Seq[TaskPlanningSolution] = {
    val tasks = workflowLists.map(_.toTask(now, workSchedule))
    val assignees = List(Assignee(0L))

    val solverManager: SolverManager[TaskSchedule, UUID] = SolverManager.create(
      SolverConfig.createFromXmlResource("solverConfig.xml"),
      new SolverManagerConfig()
    )
    val solverJob: SolverJob[TaskSchedule, UUID] = solverManager.solve(UUID.randomUUID(), TaskSchedule(assignees, tasks))
    val solution: TaskSchedule = solverJob.getFinalBestSolution

    var seq: Seq[Task] = Seq.empty
    var nextTask = solution.assignees.asScala.head._nextTask;
    while (nextTask != null) {
      seq = seq ++ Seq(nextTask)
      nextTask = nextTask._nextTask
    }
    val res = seq.zipWithIndex.map {
      case (task: Task, index: Int) => task.toTaskPlanningSolution(index)
    }
    res.foreach(task =>
      log.info(task.toString)
    )
    res
  }

  // Don't use this: Complexity is O(n!)
  def scheduleTasksNaive(now: LocalDateTime, workSchedule: WorkSchedule, workflowLists: Seq[WorkflowListTemporal]): Seq[TaskPlanningSolution] = {

    object WorkflowListsExecutionResult {
      implicit def ordering[A <: WorkflowListsExecutionResult]: Ordering[A] =
        Ordering.by(t => (t.numberOfDueDatesFailed, t.totalEndDate))
    }

    case class WorkflowListsExecutionResult(executionOrder: Seq[TaskPlanningSolution],
                                            totalEndDate: LocalDateTime,
                                            numberOfDueDatesFailed: Int)

    workflowLists.filter(_.workflowListType == WorkflowListType.ITEM).permutations.map { tasksPermutation =>
      var endDate = now
      var numberOfDueDatesFailed = 0
      // TODO make recursive function
      val result = tasksPermutation.zipWithIndex.map {
        case (wl: WorkflowListTemporal, index: Int) =>
          val startedAt = wl.startDate match {
            case Some(date) => Seq(date, endDate).max
            case _ => endDate
          }
          endDate = WorkScheduleUtil.getFinishDateRecursive(workSchedule, startedAt, wl.remainingDuration)
          if (wl.dueDate.exists(_ < endDate)) {
            numberOfDueDatesFailed = numberOfDueDatesFailed + 1
          }
          TaskPlanningSolution(
            id = wl.id,
            apiId = wl.apiId,
            title = wl.title,
            startDate = wl.startDate,
            dueDate = wl.dueDate,
            duration = wl.remainingDuration,
            startedAt = startedAt,
            finishedAt = endDate,
            dueDateKept = !wl.dueDate.exists(_ < endDate),
            index = index
          )
      }
      WorkflowListsExecutionResult(result, endDate, numberOfDueDatesFailed)
    }.toSeq.min.executionOrder
  }
}
