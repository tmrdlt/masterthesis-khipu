package de.tmrdlt.components.health

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe
import de.tmrdlt.components.health.HealthActor.{DoHealthCheck, Solve}
import de.tmrdlt.services.scheduling.{Task, TaskSchedule}
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
      val task1: Task = Task(
        id = 0L,
        startDate = LocalDateTime.of(2021, 1, 1, 12, 0),
        dueDate = LocalDateTime.of(2021, 1, 10, 12, 0),
        duration = 60
      )

      val task2: Task = Task(
        id = 1L,
        startDate = LocalDateTime.of(2021, 1, 2, 12, 0),
        dueDate = LocalDateTime.of(2021, 1, 10, 12, 0),
        duration = 60
      )

      val task3: Task = Task(
        id = 2L,
        startDate = LocalDateTime.of(2021, 1, 3, 12, 0),
        dueDate = LocalDateTime.of(2021, 1, 10, 12, 0),
        duration = 60
      )

      val task4: Task = Task(
        id = 3L,
        startDate = LocalDateTime.of(2021, 1, 1, 12, 0),
        dueDate = LocalDateTime.of(2021, 1, 10, 12, 0),
        duration = 60
      )

      val task5: Task = Task(
        id = 4L,
        startDate = LocalDateTime.of(2021, 1, 2, 12, 0),
        dueDate = LocalDateTime.of(2021, 1, 10, 12, 0),
        duration = 60
      )

      val task6: Task = Task(
        id = 5L,
        startDate = LocalDateTime.of(2021, 1, 3, 12, 0),
        dueDate = LocalDateTime.of(2021, 1, 10, 12, 0),
        duration = 60
      )

      val solverManager: SolverManager[TaskSchedule, UUID] = SolverManager.create(
        SolverConfig.createFromXmlResource("solverConfig.xml"),
        new SolverManagerConfig()
      )
      val solverJob: SolverJob[TaskSchedule, UUID] = solverManager.solve(UUID.randomUUID(), TaskSchedule(List(task1, task2, task3), List(task4, task5, task6)))
      val solution: TaskSchedule = solverJob.getFinalBestSolution

      log.info(solution.toString)

  }
}


object HealthActor {

  def props(): Props =
    Props(new HealthActor())

  val name = "HealthActor"


  case object DoHealthCheck

  case object Solve

}
