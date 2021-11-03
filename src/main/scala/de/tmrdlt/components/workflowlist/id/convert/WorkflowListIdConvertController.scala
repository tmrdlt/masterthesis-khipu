package de.tmrdlt.components.workflowlist.id.convert

import de.tmrdlt.database.workflowlist.{WorkflowList, WorkflowListDB}
import de.tmrdlt.models.{ConvertWorkflowListEntity, CreateWorkflowListEntity, WorkflowListEntity, WorkflowListType}
import de.tmrdlt.services.WorkflowListService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WorkflowListIdConvertController(workflowListDB: WorkflowListDB, workflowListService: WorkflowListService) {

  def convertWorkflowList(workflowListApiId: String,
                          convertWorkflowListEntity: ConvertWorkflowListEntity,
                          userApiId: String): Future[Int] = {
    for {
      workflowList <- workflowListDB.getWorkflowList(workflowListApiId)
      inserted <- (workflowList.listType, convertWorkflowListEntity.newListType) match {
        case (WorkflowListType.BOARD, WorkflowListType.LIST) => workflowListDB.convertWorkflowList(workflowList, convertWorkflowListEntity.newListType, userApiId)
        case (WorkflowListType.LIST, WorkflowListType.BOARD) => workflowListDB.convertWorkflowList(workflowList, convertWorkflowListEntity.newListType, userApiId)
        case (WorkflowListType.ITEM, _) => {
          val res = itemToHigher(workflowList)
          workflowListDB.convertItemToHigher(workflowList, res._1, res._2, convertWorkflowListEntity.newListType, userApiId)
        }
        case (_, WorkflowListType.ITEM) => for {
          workflowListEntity <- workflowListService.getWorkflowListEntityForId(workflowList.apiId)
          updated <- workflowListDB.convertHigherToItem(workflowList, s"${workflowList.description.getOrElse("")}${higherToItem(workflowListEntity)}".trim(), userApiId)
        } yield updated
        case _ => throw new Exception("invalid convert action")
      }
    } yield inserted
  }


  private def itemToHigher(workflowList: WorkflowList): (Seq[CreateWorkflowListEntity], String) = {
    val pattern = """(?m)(?:^\-[ ])(.*)""".r
    workflowList.description match {
      case None => (Seq.empty, "")
      case Some(descr) => {
        val items = pattern.findAllMatchIn(descr).map(res =>
          CreateWorkflowListEntity(
            title = res.group(1),
            description = None,
            listType = WorkflowListType.ITEM,
            parentApiId = Some(workflowList.apiId),
            isTemporalConstraintBoard = Some(false),
            children = Seq.empty
          )).toSeq
        val newDescr = pattern.replaceAllIn(descr, "").trim()
        (items, newDescr)
      }
    }
  }

  private def higherToItem(workflowListEntity: WorkflowListEntity): String = {
    workflowListEntity.children.sortBy(_.position).map(wl => s"\n- ${wl.title}").mkString("").trim()
  }
}