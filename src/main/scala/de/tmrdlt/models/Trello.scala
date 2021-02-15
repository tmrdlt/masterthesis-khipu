package de.tmrdlt.models

import de.tmrdlt.models.TrelloActionType.TrelloActionType
import de.tmrdlt.utils.EnumJsonConverter
import spray.json.RootJsonFormat

import java.time.LocalDateTime

trait TrelloJsonSupport extends JsonSupport {
  implicit val fetchTrelloBoardsEntityFormat: RootJsonFormat[FetchTrelloBoardsEntity] = jsonFormat1(FetchTrelloBoardsEntity)
  implicit val trelloBoardFormat: RootJsonFormat[TrelloBoard] = jsonFormat5(TrelloBoard)
  implicit val trelloListFormat: RootJsonFormat[TrelloList] = jsonFormat5(TrelloList)
  implicit val trelloCardFormat: RootJsonFormat[TrelloCard] = jsonFormat7(TrelloCard)
  implicit val trelloBoardSimpleFormat: RootJsonFormat[TrelloBoardSimple] = jsonFormat2(TrelloBoardSimple)
  implicit val trelloListSimpleFormat: RootJsonFormat[TrelloListSimple] = jsonFormat2(TrelloListSimple)
  implicit val trelloCardSimpleFormat: RootJsonFormat[TrelloCardSimple] = jsonFormat2(TrelloCardSimple)
  implicit val trelloActionTypeJsonSupport: EnumJsonConverter[TrelloActionType.type] = new EnumJsonConverter(TrelloActionType)
  implicit val trelloActionUpdateOldFormat: RootJsonFormat[TrelloActionUpdateOld] = jsonFormat2(TrelloActionUpdateOld)
  implicit val trelloActionDataSFormat: RootJsonFormat[TrelloActionData] = jsonFormat5(TrelloActionData)
  implicit val trelloActionSupportFormat: RootJsonFormat[TrelloAction] = jsonFormat5(TrelloAction)
}

case class FetchTrelloBoardsEntity(boardIds: Seq[String])

case class TrelloBoard(id: String,
                       name: String,
                       desc: String,
                       closed: Boolean,
                       url: String)

case class TrelloList(id: String,
                      name: String,
                      closed: Boolean,
                      pos: Long,
                      idBoard: String)

case class TrelloCard(id: String,
                      name: String,
                      desc: String,
                      closed: Boolean,
                      dateLastActivity: LocalDateTime,
                      idBoard: String,
                      idList: String)

case class TrelloBoardSimple(id: String,
                             name: String)

case class TrelloListSimple(id: String,
                            name: Option[String]) // Not exactly sure why but this has to be optional

case class TrelloCardSimple(id: String,
                            name: Option[String]) // Not exactly sure why but this has to be optional

case class TrelloAction(id: String,
                        `type`: TrelloActionType,
                        idMemberCreator: String,
                        date: LocalDateTime,
                        data: TrelloActionData)


case class TrelloActionData(board: TrelloBoardSimple,
                            list: Option[TrelloListSimple],
                            card: Option[TrelloCardSimple],
                            text: Option[String],
                            old: Option[TrelloActionUpdateOld])

case class TrelloActionUpdateOld(name: Option[String],
                                 desc: Option[String])


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
