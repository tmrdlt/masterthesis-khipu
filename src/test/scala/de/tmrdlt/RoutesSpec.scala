package de.tmrdlt

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{HttpMethod, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import de.tmrdlt.components.Components
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration.DurationInt

class RoutesSpec
  extends AnyWordSpec
    with Matchers
    with ScalatestRouteTest {

  implicit def default(implicit system: ActorSystem): RouteTestTimeout = RouteTestTimeout(5.seconds)

  val testEndPoints: Route = new Routes(new Components(system)).endPoints

  "Routes" when {


    "called with allowed HTTP methods" should {

      def checkAllowedRoute(allowedHttpMethods: Seq[HttpMethod], path: String): Unit = {
        for (httpMethod <- allowedHttpMethods) {
          val httpRequest = new RequestBuilder(httpMethod)(path)
          httpRequest ~> Route.seal(testEndPoints) ~> check {
            val statusIsCorrect = !status.equals(StatusCodes.NotFound)
            if (!statusIsCorrect) throw new Exception(s"Allowed route is not found: ${httpRequest.method.value} ${httpRequest.uri}")
            statusIsCorrect shouldBe true
          }
        }
      }

      "have status different to NotFound" in {
        checkAllowedRoute(Seq(GET), "/health")
        checkAllowedRoute(Seq(POST), "/fetchTrelloBoard")
      }
    }


    "called with not allowed HTTP methods" should {

      def checkMethodsNotAllowed(allowedHttpMethods: Seq[HttpMethod], path: String): Unit = {
        val allHttpMethods = Set(CONNECT, DELETE, GET, HEAD, OPTIONS, PATCH, POST, PUT, TRACE)
        val forbiddenHttpMethods = allHttpMethods.filterNot(allowedHttpMethods.contains(_))
        for (httpMethod <- forbiddenHttpMethods) {
          val httpRequest = new RequestBuilder(httpMethod)(path)
          httpRequest ~> Route.seal(testEndPoints) ~> check {
            val statusIsCorrect = status.equals(StatusCodes.MethodNotAllowed)
            if (!statusIsCorrect) throw new Exception(s"Forbidden route is found: ${httpRequest.method.value} ${httpRequest.uri}")
            statusIsCorrect shouldBe true
          }
        }
      }

      "have status MethodNotAllowed" in {
        // v1 routes
        checkMethodsNotAllowed(Seq(GET), "/health")
        checkMethodsNotAllowed(Seq(POST), "/fetchTrelloBoard")
      }
    }
  }

}
