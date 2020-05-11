package com.abaddon83.libs.akkaHttp

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.abaddon83.libs.akkaHttp.routes.RoutesBuilder

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Try

trait AkkaHttpServer {
  val host: String = "127.0.0.1"
  val port: Int = Try(System.getenv("PORT")).map(_.toInt).getOrElse(9000)

  implicit val actorSystem: ActorSystem;
  implicit val materializer: ActorMaterializer
  val apiRoutes: RoutesBuilder


  def startServer() = {
    lazy val logger = Logging(actorSystem, classOf[App])

    Http().bindAndHandle(apiRoutes.getRoute(), host, port)
    logger.info(s"Starting the HTTP server at ${port}")
    Await.result(actorSystem.whenTerminated, Duration.Inf)
  }



}
