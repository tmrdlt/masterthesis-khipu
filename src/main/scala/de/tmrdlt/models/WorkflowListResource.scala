package de.tmrdlt.models

import de.tmrdlt.constants.AssumedDurationForTasksWithoutDuration
import spray.json.RootJsonFormat

import java.time.LocalDateTime

// TODO Could lead to problems when working with frontends from different timezones as we use LocalDateTime here
case class TemporalResourceEntity(startDate: Option[LocalDateTime],
                                  dueDate: Option[LocalDateTime],
                                  durationInMinutes: Option[Long]) {
  def getDuration: Long = durationInMinutes.getOrElse(AssumedDurationForTasksWithoutDuration.value)
}

case class NumericResourceEntity(label: String,
                                 value: Float)

case class TextualResourceEntity(label: String,
                                 value: Option[String])

case class UserResourceEntity(username: Option[String])

case class WorkflowListResourceEntity(numeric: Option[Seq[NumericResourceEntity]],
                                      textual: Option[Seq[TextualResourceEntity]],
                                      temporal: Option[TemporalResourceEntity],
                                      user: Option[UserResourceEntity])

trait WorkflowListResourceJsonSupport extends JsonSupport {
  implicit val temporalResourceEntityFormat: RootJsonFormat[TemporalResourceEntity] = jsonFormat3(TemporalResourceEntity)
  implicit val numericResourceEntityFormat: RootJsonFormat[NumericResourceEntity] = jsonFormat2(NumericResourceEntity)
  implicit val textualResourceEntityFormat: RootJsonFormat[TextualResourceEntity] = jsonFormat2(TextualResourceEntity)
  implicit val userResourceEntityFormat: RootJsonFormat[UserResourceEntity] = jsonFormat1(UserResourceEntity)
  implicit val resourceEntityFormat: RootJsonFormat[WorkflowListResourceEntity] = jsonFormat4(WorkflowListResourceEntity)
}
