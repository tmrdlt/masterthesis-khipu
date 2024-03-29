package de.tmrdlt.database.workflowlistresource

import de.tmrdlt.database.MyDB._
import de.tmrdlt.database.MyPostgresProfile.api._
import de.tmrdlt.database.event.Event
import de.tmrdlt.database.user.UserDB
import de.tmrdlt.models.{EventType, WorkflowListDataSource, WorkflowListResourceEntity}
import slick.dbio.Effect
import slick.sql.SqlAction

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkflowListResourceDB {

  def getNumericResources(workflowListIds: Seq[Long]): Future[Seq[NumericResource]] =
    db.run(numericResourceQuery.filter(_.workflowListId inSet workflowListIds).result)

  def getTextualResources(workflowListIds: Seq[Long]): Future[Seq[TextualResource]] =
    db.run(textualResourceQuery.filter(_.workflowListId inSet workflowListIds).result)

  def getTemporalResources(workflowListIds: Seq[Long]): Future[Seq[TemporalResource]] =
    db.run(temporalResourceQuery.filter(_.workflowListId inSet workflowListIds).result)

  def getUserResources(workflowListIds: Seq[Long]): Future[Seq[UserResource]] =
    db.run(userResourceQuery.filter(_.workflowListId inSet workflowListIds).result)

  def insertOrUpdateWorkflowListResource(workflowListId: Long,
                                         wlr: WorkflowListResourceEntity,
                                         userApiId: String,
                                         userDB: UserDB): Future[Int] = {
    val now = LocalDateTime.now()
    val numericQuery = wlr.numeric match {
      case Some(numeric) => for {
        existingResources <- DBIO.sequence(numeric.map(entity => getNumericResourceSqlAction(workflowListId, entity.label)))
        _ <- numericResourceQuery.filter(_.workflowListId === workflowListId).delete
        numerics = numeric.map { entity =>
          val existingResource = existingResources.find(_.map(_.label).contains(entity.label)).flatten
          NumericResource(
            id = existingResource.map(_.id).getOrElse(0L),
            workflowListId = workflowListId,
            label = entity.label,
            value = entity.value,
            createdAt = existingResource.map(_.createdAt).getOrElse(now),
            updatedAt = now
          )
        }
        insertedNumericResources <- numericResourceQuery returning numericResourceQuery ++= numerics
      } yield insertedNumericResources.length
      case _ => DBIO.successful(0)
    }

    val textualQuery = wlr.textual match {
      case Some(textual) => for {
        existingResources <- DBIO.sequence(textual.map(entity => getTextualResourceSqlAction(workflowListId, entity.label)))
        _ <- textualResourceQuery.filter(_.workflowListId === workflowListId).delete
        textuals = textual.map { entity =>
          val existingResource = existingResources.find(_.map(_.label).contains(entity.label)).flatten
          TextualResource(
            id = existingResource.map(_.id).getOrElse(0L),
            workflowListId = workflowListId,
            label = entity.label,
            value = entity.value,
            createdAt = existingResource.map(_.createdAt).getOrElse(now),
            updatedAt = now
          )
        }
        insertedTextualResources <- textualResourceQuery returning textualResourceQuery ++= textuals
      } yield insertedTextualResources.length
      case _ => DBIO.successful(0)
    }

    val temporalQuery = wlr.temporal match {
      case Some(temporal) => for {
        temporalResourceOption <- getTemporalResourceSqlAction(workflowListId)
        temporalResource = TemporalResource(
          id = temporalResourceOption.map(_.id).getOrElse(0L),
          workflowListId = workflowListId,
          startDate = temporal.startDate,
          dueDate = temporal.dueDate,
          durationInMinutes = temporal.durationInMinutes,
          createdAt = temporalResourceOption.map(_.createdAt).getOrElse(now),
          updatedAt = now
        )
        inserted <- temporalResourceQuery.insertOrUpdate(temporalResource)
      } yield inserted
      case _ => DBIO.successful(0)
    }

    val userQuery = wlr.user match {
      case Some(user) => for {
        activeUserId <- user.username match {
          case Some(username) => userDB.getActiveUserByUserNameSqlAction(username).map(u => Some(u.id))
          case _ => DBIO.successful(None)
        }
        userResourceOption <- getUserResourceSqlAction(workflowListId)
        useResource = UserResource(
          id = userResourceOption.map(_.id).getOrElse(0L),
          workflowListId = workflowListId,
          userId = activeUserId,
          createdAt = userResourceOption.map(_.createdAt).getOrElse(now),
          updatedAt = now
        )
        inserted <- userResourceQuery.insertOrUpdate(useResource)
      } yield inserted
      case _ => DBIO.successful(0)
    }

    val query = for {
      numericsInserted <- numericQuery
      textualsInserted <- textualQuery
      temporalInserted <- temporalQuery
      userInserted <- userQuery
      inserted = numericsInserted + textualsInserted + temporalInserted + userInserted
      _ <- if (inserted > 0) {
        (eventQuery returning eventQuery) += {
          Event(
            id = 0L,
            apiId = java.util.UUID.randomUUID.toString,
            eventType = EventType.UPDATE_RESOURCES.toString,
            workflowListApiId = workflowListId.toString,
            resourcesUpdated = Some(inserted),
            userApiId = userApiId,
            createdAt = LocalDateTime.now(),
            dataSource = WorkflowListDataSource.Khipu
          )
        }
      } else DBIO.successful(0)
    } yield inserted

    db.run(query.transactionally)
  }

  private def getNumericResourceSqlAction(workflowListId: Long, label: String): SqlAction[Option[NumericResource], NoStream, Effect.Read] =
    numericResourceQuery
      .filter(gr => gr.workflowListId === workflowListId && gr.label === label)
      .result
      .headOption

  private def getTextualResourceSqlAction(workflowListId: Long, label: String): SqlAction[Option[TextualResource], NoStream, Effect.Read] =
    textualResourceQuery
      .filter(gr => gr.workflowListId === workflowListId && gr.label === label)
      .result
      .headOption

  private def getTemporalResourceSqlAction(workflowListId: Long): SqlAction[Option[TemporalResource], NoStream, Effect.Read] =
    temporalResourceQuery.filter(_.workflowListId === workflowListId).result.headOption

  private def getUserResourceSqlAction(workflowListId: Long): SqlAction[Option[UserResource], NoStream, Effect.Read] =
    userResourceQuery.filter(_.workflowListId === workflowListId).result.headOption


}


