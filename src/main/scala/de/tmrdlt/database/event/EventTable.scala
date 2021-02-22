package de.tmrdlt.database.event

import de.tmrdlt.models.ActionType.ActionType

import java.time.LocalDateTime

case class Event(id: Long,
                 apiId: String,
                 actionType: ActionType,
                 date: LocalDateTime)

// TODO implement