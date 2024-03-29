package de.tmrdlt.models

import de.tmrdlt.models.TrelloActionType.TrelloActionType
import de.tmrdlt.utils.EnumJsonConverter
import spray.json.RootJsonFormat

import java.time.LocalDateTime

trait TrelloJsonSupport extends JsonSupport {
  implicit val fetchDataTrelloEntityFormat: RootJsonFormat[FetchDataTrelloEntity] = jsonFormat1(FetchDataTrelloEntity)
  implicit val trelloBoardFormat: RootJsonFormat[TrelloBoard] = jsonFormat4(TrelloBoard)
  implicit val trelloListFormat: RootJsonFormat[TrelloList] = jsonFormat4(TrelloList)
  implicit val trelloCardFormat: RootJsonFormat[TrelloCard] = jsonFormat7(TrelloCard)
  implicit val trelloBoardSimpleFormat: RootJsonFormat[TrelloBoardSimple] = jsonFormat2(TrelloBoardSimple)
  implicit val trelloListSimpleIdFormat: RootJsonFormat[TrelloListSimpleId] = jsonFormat1(TrelloListSimpleId)
  implicit val trelloListSimpleFormat: RootJsonFormat[TrelloListSimple] = jsonFormat2(TrelloListSimple)
  implicit val trelloCardSimpleFormat: RootJsonFormat[TrelloCardSimple] = jsonFormat2(TrelloCardSimple)
  implicit val trelloActionTypeJsonSupport: EnumJsonConverter[TrelloActionType.type] = new EnumJsonConverter(TrelloActionType)
  implicit val trelloActionUpdateOldFormat: RootJsonFormat[TrelloActionUpdateOld] = jsonFormat2(TrelloActionUpdateOld)
  implicit val trelloActionDataFormat: RootJsonFormat[TrelloActionData] = jsonFormat6(TrelloActionData)
  implicit val trelloActionFormat: RootJsonFormat[TrelloAction] = jsonFormat5(TrelloAction)
}

case class FetchDataTrelloEntity(boardIds: Seq[String])

case class TrelloBoard(id: String,
                       name: String,
                       desc: String,
                       closed: Boolean)

case class TrelloList(id: String,
                      name: String,
                      closed: Boolean,
                      idBoard: String)

case class TrelloCard(id: String,
                      name: String,
                      desc: String,
                      closed: Boolean,
                      idBoard: String,
                      idList: String,
                      dateLastActivity: LocalDateTime)

case class TrelloBoardSimple(id: String,
                             name: Option[String])

case class TrelloListSimple(id: Either[String, TrelloListSimpleId],
                            name: Option[String]) { // Not exactly sure why but this has to be optional
  def getId: String =
    id match {
      case Left(id) => id
      case Right(idObj) => idObj._id
    }
}


case class TrelloListSimpleId(_id: String)

case class TrelloCardSimple(id: String,
                            name: Option[String]) // Not exactly sure why but this has to be optional

case class TrelloAction(id: String,
                        `type`: TrelloActionType,
                        idMemberCreator: String,
                        date: LocalDateTime,
                        data: TrelloActionData) {

  def isMoveCardToNewColumnAction: Boolean =
    `type` == TrelloActionType.updateCard && data.listAfter.isDefined && data.listBefore.isDefined

  def isCreateOrDeleteAction: Boolean =
    `type` == TrelloActionType.createCard || `type` == TrelloActionType.deleteCard

  def isCreateCardAction: Boolean =
    `type` == TrelloActionType.createCard
}

case class TrelloActionData(board: TrelloBoardSimple,
                            list: Option[TrelloListSimple],
                            card: Option[TrelloCardSimple],
                            text: Option[String],
                            // old: Option[TrelloActionUpdateOld],
                            listBefore: Option[TrelloListSimple], // If is move to new column
                            listAfter: Option[TrelloListSimple] // If is move to new column
                           )

case class TrelloActionUpdateOld(name: Option[String],
                                 desc: Option[String]) // Possible to get boolean here, evaluate that


object TrelloActionType extends Enumeration {

  type TrelloActionType = Value
  val acceptEnterpriseJoinRequest, addAdminToBoard, addAdminToOrganization, addAttachmentToCard, addChecklistToCard,
  addLabelToCard, addMemberToBoard, addMemberToCard, addMemberToOrganization, addOrganizationToEnterprise,
  addToEnterprisePluginWhitelist, addToOrganizationBoard, commentCard, convertToCardFromCheckItem, copyBoard, copyCard,
  copyChecklist, createLabel, copyCommentCard, createBoard, createBoardInvitation, createBoardPreference, createCard,
  createList, createOrganization, createOrganizationInvitation, deleteAttachmentFromCard, deleteBoardInvitation,
  deleteCard, deleteCheckItem, deleteLabel, deleteOrganizationInvitation, disableEnterprisePluginWhitelist,
  disablePlugin, disablePowerUp, emailCard, enableEnterprisePluginWhitelist, enablePlugin, enablePowerUp,
  makeAdminOfBoard, makeAdminOfOrganization, makeNormalMemberOfBoard, makeNormalMemberOfOrganization,
  makeObserverOfBoard, memberJoinedTrello, moveCardFromBoard, moveCardToBoard, moveListFromBoard, moveListToBoard,
  removeAdminFromBoard, removeAdminFromOrganization, removeChecklistFromCard, removeFromEnterprisePluginWhitelist,
  removeFromOrganizationBoard, removeLabelFromCard, removeMemberFromBoard, removeMemberFromCard,
  removeMemberFromOrganization, removeOrganizationFromEnterprise, unconfirmedBoardInvitation,
  unconfirmedOrganizationInvitation, updateBoard, updateCard, updateCheckItem, updateCheckItemStateOnCard,
  updateChecklist, updateLabel, updateList, updateMember, updateOrganization, voteOnCard = Value
}
