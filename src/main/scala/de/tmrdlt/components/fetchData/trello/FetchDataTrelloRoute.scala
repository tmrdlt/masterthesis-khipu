package de.tmrdlt.components.fetchData.trello

import akka.http.scaladsl.model.StatusCodes.Accepted
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.tmrdlt.models.{FetchDataTrelloEntity, TrelloJsonSupport}
import de.tmrdlt.utils.SimpleNameLogger

class FetchDataTrelloRoute(controller: FetchDataTrelloController)
  extends SimpleNameLogger with TrelloJsonSupport {

  val route: Route =
    post {
      entity(as[FetchDataTrelloEntity]) { fetchDataTrelloEntity =>
        controller.fetchDataTrello(fetchDataTrelloEntity.boardIds)
        complete(Accepted)
      }
    }
}
