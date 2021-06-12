package de.tmrdlt.models

import de.tmrdlt.utils.EnumJsonConverter


trait EnumJsonSupport extends JsonSupport {
  implicit val workflowListStateFormat: EnumJsonConverter[WorkflowListState.type] = new EnumJsonConverter(WorkflowListState)
  implicit val workflowUseCaseFormat: EnumJsonConverter[WorkflowListUseCase.type] = new EnumJsonConverter(WorkflowListUseCase)
  implicit val workflowDataSourceFormat: EnumJsonConverter[WorkflowListDataSource.type] = new EnumJsonConverter(WorkflowListDataSource)
  implicit val workflowListTypeFormat: EnumJsonConverter[WorkflowListType.type] = new EnumJsonConverter(WorkflowListType)
  implicit val eventTypeFormat: EnumJsonConverter[EventType.type] = new EnumJsonConverter(EventType)
}

object WorkflowListState extends Enumeration {
  type WorkflowListState = Value
  val OPEN, CLOSED = Value

  def getWorkflowListState(state: String): Option[WorkflowListState] =
    state match {
      case "open" => Some(WorkflowListState.OPEN)
      case "closed" => Some(WorkflowListState.CLOSED)
      case _ => None
    }

  def getWorkflowListState(closed: Boolean): WorkflowListState = {
    if (closed) WorkflowListState.CLOSED else WorkflowListState.OPEN
  }
}

object WorkflowListUseCase extends Enumeration {
  type WorkflowListUseCase = Value
  val softwareDevelopment, roadmap, personal = Value
}

object WorkflowListDataSource extends Enumeration {
  type WorkflowListDataSource = Value
  val Khipu, GitHub, Trello = Value
}

object WorkflowListType extends Enumeration {
  type WorkflowListType = Value
  val ITEM, LIST, BOARD = Value

  def getUsageType(children: Seq[WorkflowListEntity]): WorkflowListType = {
    if (children.isEmpty) {
      WorkflowListType.ITEM
    } else if (children.forall(l => l.children.isEmpty)) {
      WorkflowListType.LIST
    } else {
      WorkflowListType.BOARD
    }
  }
}

object EventType extends Enumeration {
  type EventType = Value
  val createWorkflowList, deleteWorkflowList, moveToNewParent = Value
}
