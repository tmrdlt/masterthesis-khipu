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
    var nextTask = solution.assignees.asScala.head.nextTask
    while (nextTask != null) {
      seq = seq ++ Seq(nextTask)
      nextTask = nextTask.nextTask
    }
    val res = seq.zipWithIndex.map {
      case (task: Task, index: Int) => task.toTaskPlanningSolution(index)
    }
    res.foreach(task =>
      log.info(task.toString)
    )
    res
  }

  @deprecated("Complexity is O(n!), do not use in production!", "01-10-2021")
  def scheduleTasksNaive(now: LocalDateTime, workSchedule: WorkSchedule, workflowLists: Seq[WorkflowListTemporal]): Seq[TaskPlanningSolution] = {
    workflowLists.filter(_.workflowListType == WorkflowListType.ITEM).permutations.map { tasksPermutation =>
      evaluateTasksPermutation(now, workSchedule, tasksPermutation)
    }.toSeq.min.executionOrder
  }

  // For evaluation of user study
  def getAllBestSolutions(now: LocalDateTime, workSchedule: WorkSchedule, workflowLists: Seq[WorkflowListTemporal]): Seq[Seq[TaskPlanningSolution]] = {
    val all = workflowLists.filter(_.workflowListType == WorkflowListType.ITEM).permutations.map { tasksPermutation =>
      log.info(s"${tasksPermutation.map(_.id)}")
      evaluateTasksPermutation(now, workSchedule, tasksPermutation)
    }.toSeq
    val min = all.min
    all.filter(res => res.totalEndDate == min.totalEndDate && res.numberOfDueDatesFailed == min.numberOfDueDatesFailed).map(_.executionOrder)
  }

  // For evaluation of user study
  def evaluatePlanningOrder(now: LocalDateTime,
                            workSchedule: WorkSchedule,
                            workflowLists: Seq[WorkflowListTemporal]): (LocalDateTime, Int) = {
    val evaluation = evaluateTasksPermutation(now, workSchedule, workflowLists)
    (evaluation.totalEndDate, evaluation.numberOfDueDatesFailed)
  }

  private object WorkflowListsExecutionResult {
    implicit def ordering[A <: WorkflowListsExecutionResult]: Ordering[A] =
      Ordering.by(t => (t.numberOfDueDatesFailed, t.totalEndDate))
  }

  private case class WorkflowListsExecutionResult(executionOrder: Seq[TaskPlanningSolution],
                                                  totalEndDate: LocalDateTime,
                                                  numberOfDueDatesFailed: Int)

  private def evaluateTasksPermutation(now: LocalDateTime,
                                       workSchedule: WorkSchedule,
                                       workflowLists: Seq[WorkflowListTemporal]): WorkflowListsExecutionResult = {
    var endDate = now
    var numberOfDueDatesFailed = 0
    val result = workflowLists.zipWithIndex.map {
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
  }
}
