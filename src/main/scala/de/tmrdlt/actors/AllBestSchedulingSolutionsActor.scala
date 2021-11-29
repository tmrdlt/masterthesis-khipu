package de.tmrdlt.actors

import akka.actor.{Actor, ActorLogging, Props}
import de.tmrdlt.actors.AllBestSchedulingSolutionsActor.GetBestAllSchedulingSolutions
import de.tmrdlt.database.workschedule.WorkSchedule
import de.tmrdlt.models.{JsonSupport, WorkflowListTemporal}
import de.tmrdlt.services.SchedulingService
import spray.json.{RootJsonFormat, enrichAny}

import java.io.{BufferedWriter, FileWriter}
import java.time.LocalDateTime

class AllBestSchedulingSolutionsActor(schedulingService: SchedulingService) extends Actor with ActorLogging with JsonSupport {

  override def receive: PartialFunction[Any, Unit] = {

    case GetBestAllSchedulingSolutions(now, workSchedule, workflowLists) =>
      case class AllBestSolutions(sols: Seq[Seq[Long]])
      implicit val allBestSolutionsFormat: RootJsonFormat[AllBestSolutions] = jsonFormat1(AllBestSolutions)

      log.info("Start calulating solutions")
      val res = schedulingService.getAllBestSolutions(now, workSchedule, workflowLists)
      val solutions = AllBestSolutions(res.map(_.map(_.id)))
      val w = new BufferedWriter(new FileWriter("sols.json"))
      w.write(solutions.toJson.prettyPrint)
      w.close()

  }
}

object AllBestSchedulingSolutionsActor {

  def props(schedulingService: SchedulingService): Props =
    Props(new AllBestSchedulingSolutionsActor(schedulingService))

  val name = "AllBestSchedulingSolutionsActor"

  case class GetBestAllSchedulingSolutions(now: LocalDateTime, workSchedule: WorkSchedule, workflowLists: Seq[WorkflowListTemporal])

}