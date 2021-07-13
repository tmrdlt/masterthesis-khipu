package de.tmrdlt.database.workflowlistresource

import de.tmrdlt.database.MyDB._
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.WorkflowListResource

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkflowListResourceDB {

  def getTemporalResources: Future[Seq[TemporalResource]] =
    db.run(temporalResourceQuery.result)

  def getTemporalResources(workflowListIds: Seq[Long]): Future[Seq[TemporalResource]] =
    db.run(temporalResourceQuery.filter(_.workflowListId inSet workflowListIds).result)

  def getTemporalResource(workflowListId: Long): Future[Option[TemporalResource]] =
    db.run(temporalResourceQuery.filter(_.workflowListId === workflowListId).result.headOption)

  def getGenericResources: Future[Seq[GenericResource]] =
    db.run(genericResourceQuery.result)

  def getGenericResources(workflowListIds: Seq[Long]): Future[Seq[GenericResource]] =
    db.run(genericResourceQuery.filter(_.workflowListId inSet workflowListIds).result)

  def getGenericResource(workflowListId: Long, label: String): Future[Option[GenericResource]] =
    db.run(
      genericResourceQuery
        .filter(gr => gr.workflowListId === workflowListId && gr.label === label)
        .result
        .headOption
    )

  def deleteGenericResource(workflowListId: Long, label: String): Future[Int] =
    db.run(genericResourceQuery.filter(gr => gr.workflowListId === workflowListId && gr.label === label).delete)

  def insertOrUpdateWorkflowListResource[T <: WorkflowListResource](workflowListResource: T): Future[Int] = {
    val query = workflowListResource match {
      case gr: GenericResource => genericResourceQuery.insertOrUpdate(gr)
      case tr: TemporalResource => temporalResourceQuery.insertOrUpdate(tr)
    }
    db.run(query)
  }

  def updateGenericResources(workflowListId: Long, genericResources: Seq[GenericResource]): Future[Int] = {
    val query = for {
      _ <- genericResourceQuery.filter(_.workflowListId === workflowListId).delete
      insertedGenericResources <- genericResourceQuery returning genericResourceQuery ++= genericResources
    } yield {
      insertedGenericResources.length
    }
    db.run(query.transactionally)
  }

}
