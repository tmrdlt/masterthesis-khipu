package de.tmrdlt.database.workflowlistresource

import de.tmrdlt.models.UserResourceEntity

import java.time.LocalDateTime

case class UserResource(id: Long,
                        workflowListId: Long,
                        userId: Long,
                        createdAt: LocalDateTime,
                        updatedAt: LocalDateTime) {

  def toUserResourceEntity: UserResourceEntity =
    UserResourceEntity(userId = userId)
}
