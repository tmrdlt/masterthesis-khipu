package de.tmrdlt.models

import spray.json.RootJsonFormat

import java.time.LocalDateTime

// TODO Could lead to problems when working with frontends from different timezones as we use LocalDateTime here
case class TemporalResourceEntity(startDate: Option[LocalDateTime],
                                  endDate: Option[LocalDateTime],
                                  durationInMinutes: Option[Long])

case class NumericResourceEntity(label: String,
                                 value: Float)

case class TextualResourceEntity(label: String,
                                 value: Option[String])

case class UserResourceEntity(username: Option[String])

case class WorkflowListResourceEntity(numeric: Option[Seq[NumericResourceEntity]],
                                      textual: Option[Seq[TextualResourceEntity]],
                                      temporal: Option[TemporalResourceEntity],
                                      user: Option[UserResourceEntity])

case class TemporalQueryResultEntity(totalDurationMinutes: Long,
                                     totalFinishDateByDuration: LocalDateTime,
                                     openTasksPredictedFinishDate: LocalDateTime,
                                     bestExecutionOrder: Seq[String],
                                     numberOfFailedDueDates: Int)

trait WorkflowListResourceJsonSupport extends JsonSupport {
  implicit val temporalResourceEntityFormat: RootJsonFormat[TemporalResourceEntity] = jsonFormat3(TemporalResourceEntity)
  implicit val numericResourceEntityFormat: RootJsonFormat[NumericResourceEntity] = jsonFormat2(NumericResourceEntity)
  implicit val textualResourceEntityFormat: RootJsonFormat[TextualResourceEntity] = jsonFormat2(TextualResourceEntity)
  implicit val userResourceEntityFormat: RootJsonFormat[UserResourceEntity] = jsonFormat1(UserResourceEntity)
  implicit val resourceEntityFormat: RootJsonFormat[WorkflowListResourceEntity] = jsonFormat4(WorkflowListResourceEntity)
  implicit val temporalQueryResultEntityFormat: RootJsonFormat[TemporalQueryResultEntity] = jsonFormat5(TemporalQueryResultEntity)
}
