package com.abaddon83.legal

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.abaddon83.legal.contracts.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.FileRepositoryAdapters.Fake.FakeFileRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors.ContractActor
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.http.ContractRoutes
import com.abaddon83.legal.contracts.adapters.ddMandateAdapters.internal.DDMandateInternalAdapter
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, DDMandatePort, FileRepositoryPort}
import com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.akka.actors.DDMandateActor
import com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.akka.actors.http.DDMandateRoutes
import com.abaddon83.libs.akkaHttp.routes.RouteExceptionHandling

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Try

trait AkkaHttpServer extends  RouteExceptionHandling{


  implicit val actorSystem: ActorSystem = ActorSystem(name = "DDMandate")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ddMandatePort : DDMandatePort = new DDMandateInternalAdapter //bind[DDMandatePort]
  implicit val contractRepositoryPort : ContractRepositoryPort = new FakeContractRepositoryAdapter //bind[ContractRepositoryPort]
  implicit val fileRepositoryPort : FileRepositoryPort = new FakeFileRepositoryAdapter //bind[FileRepositoryPort]
  lazy val logger = Logging(actorSystem, classOf[App])

//val ref = context.actorOf(SomeActor.props(someLong)), "actor")
  val contractActor = actorSystem.actorOf(ContractActor.props(),name = "contractActor")
  val ddMandateActor = actorSystem.actorOf(DDMandateActor.props(),name = "ddMandateActor")



  logger.info(s"contractActor.path.toString: ${contractActor.path.toString}")
  logger.info(s"ddMandateActor.path.toString: ${ddMandateActor.path.toString}")


  val host: String = "127.0.0.1"
  val port: Int = Try(System.getenv("PORT")).map(_.toInt).getOrElse(9000)

  val ddMandateRoutes = new DDMandateRoutes()
  val contractRoutes = new ContractRoutes()

  lazy val routes: Route = handleExceptions(globalExceptionHandler){
    pathPrefix("api") {
      concat(
        contractRoutes.routes,
        ddMandateRoutes.route
      )
    }
  }

  def startServer() = {


    Http().bindAndHandle(routes, host, port)
    logger.info(s"Starting the HTTP server at ${port}")
    Await.result(actorSystem.whenTerminated, Duration.Inf)
  }
}