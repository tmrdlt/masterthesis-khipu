package de.tmrdlt.models

import de.tmrdlt.utils.EnumJsonConverter


trait UsageTypeJsonSupport extends JsonSupport {

  implicit val usageTypeJsonSupport: EnumJsonConverter[UsageType.type] = new EnumJsonConverter(UsageType)
}

object UsageType extends Enumeration {

  type UsageType = Value
  val TASK, LIST, PROJECT = Value

  def getUsageType(children: Seq[WorkflowListEntity]): UsageType = {
    if (children.isEmpty) {
      UsageType.TASK
    } else if (children.forall(l => l.children.isEmpty)) {
      UsageType.LIST
    } else {
      UsageType.PROJECT
    }
  }
}
