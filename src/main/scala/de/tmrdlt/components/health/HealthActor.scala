package de.tmrdlt.components.health

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe
import de.tmrdlt.components.health.HealthActor.{DoHealthCheck, Solve}
import de.tmrdlt.services.scheduling.{Employee, Task, TaskSchedule}
import org.optaplanner.core.api.solver.{SolverJob, SolverManager}
import org.optaplanner.core.config.solver.{SolverConfig, SolverManagerConfig}

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class HealthActor() extends Actor with ActorLogging {

  override def receive: PartialFunction[Any, Unit] = {

    case DoHealthCheck =>
      pipe(
        Future.successful(true)
      ) to sender()


    case Solve =>
      val tasks = List(
        Task(
          id = 0L,
          startDate = LocalDateTime.of(2021, 1, 1, 12, 0),
          dueDate = LocalDateTime.of(2021, 1, 1, 13, 0),
          duration = 60),
        Task(
          id = 1L,
          startDate = LocalDateTime.of(2021, 1, 1, 13, 0),
          dueDate = LocalDateTime.of(2021, 1, 1, 14, 0),
          duration = 60),
        Task(
          id = 2L,
          startDate = LocalDateTime.of(2021, 1, 1, 11, 0),
          dueDate = LocalDateTime.of(2021, 1, 1, 12, 0),
          duration = 60))

      val employees = List(Employee(0L))

      val solverManager: SolverManager[TaskSchedule, UUID] = SolverManager.create(
        SolverConfig.createFromXmlResource("solverConfig.xml"),
        new SolverManagerConfig()
      )
      val solverJob: SolverJob[TaskSchedule, UUID] = solverManager.solve(UUID.randomUUID(), TaskSchedule(employees, tasks))
      val solution: TaskSchedule = solverJob.getFinalBestSolution

      log.info(solution.tasks.toString)


  }
}


object HealthActor {

  def props(): Props =
    Props(new HealthActor())

  val name = "HealthActor"


  case object DoHealthCheck

  case object Solve

}
