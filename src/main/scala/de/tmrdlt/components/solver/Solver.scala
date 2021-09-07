package de.tmrdlt.components.solver

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{complete, get}
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{ApiErrorJsonSupport, JsonSupport}
import de.tmrdlt.services.scheduling.{Employee, Task, TaskSchedule}
import de.tmrdlt.utils.SimpleNameLogger
import org.optaplanner.core.api.solver.{SolverJob, SolverManager}
import org.optaplanner.core.config.solver.{SolverConfig, SolverManagerConfig}
import spray.json.RootJsonFormat

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.jdk.CollectionConverters._

class SolverRoute(controller: SolverController) extends ApiErrorJsonSupport
  with SolverJsonSupport
  with SimpleNameLogger {

  val route: Route = {
    get {
      complete {
        controller.solve().map(res => OK -> res) // TODO maybe use dispatcher for blocking operation?
      }
    }
  }
}

class SolverController extends SimpleNameLogger {

  val tasks = List(
    Task(
      id = 0L,
      startDate = LocalDateTime.of(2021, 1, 1, 10, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 12, 0),
      duration = 60),
    Task(
      id = 1L,
      startDate = LocalDateTime.of(2021, 1, 1, 9, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 12, 0),
      duration = 60),
    Task(
      id = 2L,
      startDate = LocalDateTime.of(2021, 1, 1, 8, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 12, 0),
      duration = 30),
    Task(
      id = 3L,
      startDate = LocalDateTime.of(2021, 1, 1, 7, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 12, 0),
      duration = 120),
    Task(
      id = 4L,
      startDate = LocalDateTime.of(2021, 1, 1, 6, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 12, 0),
      duration = 30),
    Task(
      id = 5L,
      startDate = LocalDateTime.of(2021, 1, 1, 5, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 12, 0),
      duration = 120),
    Task(
      id = 6L,
      startDate = LocalDateTime.of(2021, 1, 1, 4, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 5, 0),
      duration = 60),
    Task(
      id = 7L,
      startDate = LocalDateTime.of(2021, 1, 1, 3, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 4, 0),
      duration = 20),
    Task(
      id = 8L,
      startDate = LocalDateTime.of(2021, 1, 1, 1, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 2, 0),
      duration = 60),
    Task(
      id = 9L,
      startDate = LocalDateTime.of(2021, 1, 1, 1, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 2, 0),
      duration = 60),
    Task(
      id = 10L,
      startDate = LocalDateTime.of(2021, 1, 1, 10, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 12, 0),
      duration = 60),
    Task(
      id = 11L,
      startDate = LocalDateTime.of(2021, 1, 1, 9, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 12, 0),
      duration = 60),
    Task(
      id = 12L,
      startDate = LocalDateTime.of(2021, 1, 1, 8, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 12, 0),
      duration = 30),
    Task(
      id = 13L,
      startDate = LocalDateTime.of(2021, 1, 1, 7, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 12, 0),
      duration = 120),
    Task(
      id = 14L,
      startDate = LocalDateTime.of(2021, 1, 1, 6, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 12, 0),
      duration = 30),
    Task(
      id = 15L,
      startDate = LocalDateTime.of(2021, 1, 1, 5, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 12, 0),
      duration = 120),
    Task(
      id = 16L,
      startDate = LocalDateTime.of(2021, 1, 1, 4, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 5, 0),
      duration = 60),
    Task(
      id = 17L,
      startDate = LocalDateTime.of(2021, 1, 1, 3, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 4, 0),
      duration = 20),
    Task(
      id = 18L,
      startDate = LocalDateTime.of(2021, 1, 1, 1, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 2, 0),
      duration = 60),
    Task(
      id = 19L,
      startDate = LocalDateTime.of(2021, 1, 1, 1, 0),
      dueDate = LocalDateTime.of(2021, 1, 1, 2, 0),
      duration = 60)
  )

  val employees = List(Employee(0L))

  def solve(): Future[Seq[TaskPlanningSolution]] = {
    Future {
      val solverManager: SolverManager[TaskSchedule, UUID] = SolverManager.create(
        SolverConfig.createFromXmlResource("solverConfig.xml"),
        new SolverManagerConfig()
      )
      val solverJob: SolverJob[TaskSchedule, UUID] = solverManager.solve(UUID.randomUUID(), TaskSchedule(employees, tasks))
      val solution: TaskSchedule = solverJob.getFinalBestSolution

      //log.info(solution.tasks.toString)

      val res = solution.tasks.asScala.toSeq.sortBy(_._startedAt).map(_.toTaskPlanningSolution)
      res.foreach(task =>
        log.info(task.toString)
      )
      res
    }

  }
}

case class TaskPlanningSolution(id: Long,
                                startedAt: LocalDateTime,
                                finishedAt: LocalDateTime,
                                dueDateKept: Boolean)

trait SolverJsonSupport extends JsonSupport {
  implicit val taskPlanningSolutionFormat: RootJsonFormat[TaskPlanningSolution] = jsonFormat4(TaskPlanningSolution)
}