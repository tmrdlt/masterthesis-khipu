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

  private val now = LocalDateTime.of(2021, 7, 1, 11, 0)
  private val employees = List(Employee(0L))
  private val tasks = List(
    Task(
      id = 0L,
      now = now,
      startDate = Some(LocalDateTime.of(2021, 7, 5, 10, 0)),
      dueDate = Some(LocalDateTime.of(2021, 7, 5, 18, 0)),
      duration = 480),
    Task(
      id = 1L,
      now = now,
      startDate = None,
      dueDate = Some(LocalDateTime.of(2021, 7, 1, 13, 0)),
      duration = 60),
    Task(
      id = 2L,
      now = now,
      startDate = None,
      dueDate = None,
      duration = 120),
  )

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
                                dueDate: Option[LocalDateTime],
                                startDate: Option[LocalDateTime],
                                duration: Long,
                                dueDateKept: Boolean)

trait SolverJsonSupport extends JsonSupport {
  implicit val taskPlanningSolutionFormat: RootJsonFormat[TaskPlanningSolution] = jsonFormat7(TaskPlanningSolution)
}