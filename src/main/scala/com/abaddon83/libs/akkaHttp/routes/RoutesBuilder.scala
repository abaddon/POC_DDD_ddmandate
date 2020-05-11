package com.abaddon83.libs.akkaHttp.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route


class RoutesBuilder(routeList: List[Route])(implicit val actorSystem: ActorSystem)
    extends Routes with RouteExceptionHandling {


  lazy val routes: Route = handleExceptions(globalExceptionHandler){
        pathPrefix("api") {
          concat(routeList:_*)
        }
    }
}

