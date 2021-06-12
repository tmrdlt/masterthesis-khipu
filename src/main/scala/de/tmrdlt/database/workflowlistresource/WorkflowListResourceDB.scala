package de.tmrdlt.database.workflowlistresource

import de.tmrdlt.database.MyDB._
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.models.WorkflowListResource

import scala.concurrent.Future

class WorkflowListResourceDB {

  def getTemporalResources: Future[Seq[TemporalResource]] =
    db.run(temporalResourceTable.result)

  def getTemporalResources(workflowListIds: Seq[Long]): Future[Seq[TemporalResource]] =
    db.run(temporalResourceTable.filter(_.workflowListId inSet workflowListIds).result)

  def getTemporalResource(workflowListId: Long): Future[Option[TemporalResource]] =
    db.run(temporalResourceTable.filter(_.workflowListId === workflowListId).result.headOption)

  def getGenericResources: Future[Seq[GenericResource]] =
    db.run(genericResourceTable.result)

  def getGenericResources(workflowListIds: Seq[Long]): Future[Seq[GenericResource]] =
    db.run(genericResourceTable.filter(_.workflowListId inSet workflowListIds).result)

  def getGenericResource(workflowListId: Long, label: String): Future[Option[GenericResource]] =
    db.run(
      genericResourceTable
        .filter(gr => gr.workflowListId === workflowListId && gr.label === label)
        .result
        .headOption
    )

  def deleteGenericResource(workflowListId: Long, label: String): Future[Int] =
    db.run(genericResourceTable.filter(gr => gr.workflowListId === workflowListId && gr.label === label).delete)

  def insertOrUpdateWorkflowListResource[T <: WorkflowListResource](workflowListResource: T): Future[Int] = {
    val query = workflowListResource match {
      case gr: GenericResource => genericResourceTable.insertOrUpdate(gr)
      case tr: TemporalResource => temporalResourceTable.insertOrUpdate(tr)
    }
    db.run(query)
  }

}
