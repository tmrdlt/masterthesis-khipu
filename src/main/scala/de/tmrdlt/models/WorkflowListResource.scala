package de.tmrdlt.models

import spray.json.RootJsonFormat

import java.time.LocalDateTime

trait WorkflowListResource {
  def id: Long

  def workflowListId: Long

  def createdAt: LocalDateTime

  def updatedAt: LocalDateTime
}

// TODO Could lead to problems when working with frontends from different timezones as we use LocalDateTime here
case class TemporalResourceEntity(startDate: Option[LocalDateTime],
                                  endDate: Option[LocalDateTime],
                                  durationInMinutes: Option[Long],
                                  connectedWorkflowListApiId: Option[String])

case class GenericResourceEntity(label: String,
                                 value: Float)

trait WorkflowListResourceJsonSupport extends JsonSupport {
  implicit val temporalResourceEntityFormat: RootJsonFormat[TemporalResourceEntity] = jsonFormat4(TemporalResourceEntity)
  implicit val genericResourceEntityFormat: RootJsonFormat[GenericResourceEntity] = jsonFormat2(GenericResourceEntity)
}
