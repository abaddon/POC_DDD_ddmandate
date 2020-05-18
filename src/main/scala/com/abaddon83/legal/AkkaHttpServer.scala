package com.abaddon83.legal

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.abaddon83.legal.contracts.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapterSingleton
import com.abaddon83.legal.contracts.adapters.FileRepositoryAdapters.Fake.FakeFileRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors.ContractControllerActor
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.http.ContractControllerRoutes
import com.abaddon83.legal.contracts.adapters.ddMandateAdapters.internal.DDMandateInternalAdapter
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, DDMandatePort, FileRepositoryPort}
import com.abaddon83.legal.ddMandates.adapters.CreditorAdapters.fake.FakeCreditorAdapter
import com.abaddon83.legal.ddMandates.adapters.bankAccountAdapters.fake.FakeBankAccountAdapter
import com.abaddon83.legal.ddMandates.adapters.contractDDMandateAdapters.internal.ContractDDMandateInternalAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.akka.actors.DDMandateControllerActor
import com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.akka.actors.http.DDMandateControllerRoutes
import com.abaddon83.legal.ddMandates.adapters.ddMandateRepositoryAdapters.fake.FakeDDMandateRepositoryAdapterSingleton
import com.abaddon83.legal.ddMandates.ports.{BankAccountPort, ContractDDMandatePort, CreditorPort, DDMandateRepositoryPort}
import com.abaddon83.libs.akkaHttp.routes.RouteExceptionHandling

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Try

trait AkkaHttpServer extends  RouteExceptionHandling{


  implicit val actorSystem: ActorSystem = ActorSystem(name = "DDMandate")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  //Implicit X DDMANDATE
  implicit val bankAccountPort: BankAccountPort = new FakeBankAccountAdapter()
  implicit val contractPort :ContractDDMandatePort = new ContractDDMandateInternalAdapter()
  implicit val creditorPort : CreditorPort = new FakeCreditorAdapter()
  implicit val ddMandateRepositoryPort : DDMandateRepositoryPort = FakeDDMandateRepositoryAdapterSingleton

  //Implicit X Contract
  implicit val ddMandatePort : DDMandatePort = new DDMandateInternalAdapter()
  implicit val contractRepositoryPort : ContractRepositoryPort =  FakeContractRepositoryAdapterSingleton
  implicit val fileRepositoryPort : FileRepositoryPort = new FakeFileRepositoryAdapter

  lazy val logger = Logging(actorSystem, classOf[App])

//val ref = context.actorOf(SomeActor.props(someLong)), "actor")
  val contractActor = actorSystem.actorOf(ContractControllerActor.props(),name = "contractActor")
  val ddMandateActor = actorSystem.actorOf(DDMandateControllerActor.props(),name = "ddMandateActor")



  logger.info(s"contractActor.path.toString: ${contractActor.path.toString}")
  logger.info(s"ddMandateActor.path.toString: ${ddMandateActor.path.toString}")


  val host: String = "127.0.0.1"
  val port: Int = Try(System.getenv("PORT")).map(_.toInt).getOrElse(9000)


  val ddMandateRoutes = new DDMandateControllerRoutes()
  val contractRoutes = new ContractControllerRoutes()

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