package de.tmrdlt.services

import de.tmrdlt.components.solver.TaskPlanningSolution
import de.tmrdlt.components.workflowlist.id.query.WorkflowListTemporalQuery
import de.tmrdlt.models.{WorkflowListExecution, WorkflowListType, WorkflowListsExecutionResult}
import de.tmrdlt.services.scheduling.{Assignee, Task, TaskSchedule}
import de.tmrdlt.utils.{SimpleNameLogger, WorkScheduleUtil}
import org.optaplanner.core.api.solver.{SolverJob, SolverManager}
import org.optaplanner.core.config.solver.{SolverConfig, SolverManagerConfig}

import java.time.LocalDateTime
import java.util.UUID
import scala.jdk.CollectionConverters.IterableHasAsScala
import scala.math.Ordering.Implicits.infixOrderingOps

class SchedulingService extends SimpleNameLogger {

  def scheduleTasks(now: LocalDateTime, tasks: Seq[Task]): Seq[TaskPlanningSolution] = {
    val assignees = List(Assignee(0L))

    val solverManager: SolverManager[TaskSchedule, UUID] = SolverManager.create(
      SolverConfig.createFromXmlResource("solverConfig.xml"),
      new SolverManagerConfig()
    )
    val solverJob: SolverJob[TaskSchedule, UUID] = solverManager.solve(UUID.randomUUID(), TaskSchedule(assignees, tasks))
    val solution: TaskSchedule = solverJob.getFinalBestSolution

    val res = solution.tasks.asScala.toSeq.sortBy(_._startedAt).map(_.toTaskPlanningSolution)
    res.foreach(task =>
      log.info(task.toString)
    )
    res
  }

  // Don't use this: Complexity is O(n!)
  def scheduleTasksNaive(now: LocalDateTime, workflowLists: Seq[WorkflowListTemporalQuery]): WorkflowListsExecutionResult = {
    workflowLists.filter(_.workflowListType == WorkflowListType.ITEM).permutations.map { tasksPermutation =>
      var endDate = now
      var numberOfDueDatesFailed = 0
      // TODO make recursive function
      val result = tasksPermutation.map { workflowList =>
        val startDate = workflowList.temporalResource.flatMap(_.startDate) match {
          case Some(date) => Seq(date, endDate).max
          case _ => endDate
        }
        endDate = WorkScheduleUtil.getFinishDateRecursive(startDate, workflowList.predictedDuration)
        if (workflowList.temporalResource.flatMap(_.endDate).exists(_ < endDate)) {
          numberOfDueDatesFailed = numberOfDueDatesFailed + 1
        }
        //log.info(s"${workflowList.title}, StartDate: $startDate, EndDate: $endDate")
        WorkflowListExecution(
          apiId = workflowList.apiId,
          title = workflowList.title,
          duration = workflowList.predictedDuration,
          endDate = endDate,
          dueDate = workflowList.temporalResource.flatMap(_.endDate),
          dueDateKept = !workflowList.temporalResource.flatMap(_.endDate).exists(_ < endDate)
        )
      }
      WorkflowListsExecutionResult(result, endDate, numberOfDueDatesFailed)
    }.toSeq.min
  }
}
