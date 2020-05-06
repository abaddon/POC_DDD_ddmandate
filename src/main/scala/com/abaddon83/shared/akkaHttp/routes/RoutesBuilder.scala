package com.abaddon83.shared.akkaHttp.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route

class RoutesBuilder(routeList: List[Route])(implicit val actorSystem: ActorSystem)
    extends Routes with RouteExceptionHandling with RouteRejectionHandler {

  //protected val DefaultDataWaitTime: FiniteDuration = 1.minute

  /*lazy val routes: Route = toStrictEntity(DefaultDataWaitTime) {
    cors() {
      defaultRoutes
    }
  }*/

  lazy val routes: Route = pathPrefix("api") {
    concat(routeList:_*)
  }


}

