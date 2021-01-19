package de.tmrdlt.database.nestingrelation

import de.tmrdlt.database.BaseTableLong
import de.tmrdlt.database.MyPostgresProfile.api._
import slick.lifted.ProvenShape
import slick.sql.SqlProfile.ColumnOption.NotNull

/**
 * Database representation of a nestingRelation.
 *
 * @param id          Database id
 * @param parentId       TBD
 * @param childId TBD
 */
case class NestingRelation(id: Long,
                           parentId: Long,
                           childId: Long)

class NestingRelationTable(tag: Tag)
  extends BaseTableLong[NestingRelation](tag, "nesting_relation") {

  def parentId: Rep[Long] = column[Long]("parent_id", NotNull)

  def childId: Rep[Long] = column[Long]("child_id", NotNull)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  def * : ProvenShape[NestingRelation] =
    (id, parentId, childId).mapTo[NestingRelation]
}
