package com.abaddon83.legal

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.abaddon83.legal.contracts.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapterSingleton
import com.abaddon83.legal.contracts.adapters.contractUIAdapters.actors.ContractUIActor
import com.abaddon83.legal.contracts.adapters.contractUIAdapters.akka.akkHttp.ContractUIRoutes
import com.abaddon83.legal.contracts.adapters.ddMandateAdapters.akka.DDMandateAkkaAdapter
import com.abaddon83.legal.contracts.adapters.fileDocumentAdapters.akka.FileDocumentAkkaAdapter
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, DDMandatePort, FileDocumentPort}
import com.abaddon83.legal.ddMandates.adapters.CreditorAdapters.fake.CreditorFakeAdapter
import com.abaddon83.legal.ddMandates.adapters.bankAccountAdapters.fake.BankAccountFakeAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateContractAdapters.akka.DDMandateContractAkkaAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateRepositoryAdapters.fake.FakeDDMandateRepositoryAdapterSingleton
import com.abaddon83.legal.ddMandates.adapters.ddMandateUIAdapters.akka.DDMandateUIActor
import com.abaddon83.legal.ddMandates.adapters.ddMandateUIAdapters.akkaHttp.DDMandateUIRoutes
import com.abaddon83.legal.ddMandates.ports.{BankAccountPort, CreditorPort, DDMandateContractPort, DDMandateRepositoryPort}
import com.abaddon83.legal.fileDocuments.adapters.documentTemplateRepositoryAdapters.local.FileDocumentTemplateRepositoryLocalAdapter
import com.abaddon83.legal.fileDocuments.adapters.fileBodyAdapters.pdfBox.FileBodyPdfBoxAdapter
import com.abaddon83.legal.fileDocuments.adapters.fileDocumentRepositoryAdapters.local.FileDocumentRepositoryLocalAdapter
import com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapters.akka.FileDocumentUIActorAdapter
import com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapters.http.FileDocumentUIHttpAdapter
import com.abaddon83.legal.fileDocuments.ports.{FileDocumentTemplateRepositoryPort, FileBodyPort, FileDocumentRepositoryPort}
import com.abaddon83.libs.akkaHttp.routes.RouteExceptionHandling

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Try

trait AkkaHttpServer extends  RouteExceptionHandling{


  implicit val actorSystem: ActorSystem = ActorSystem(name = "DDMandate")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  //Implicit X DDMANDATE
  implicit lazy val bankAccountPort: BankAccountPort = new BankAccountFakeAdapter()
  implicit lazy val contractPort :DDMandateContractPort = new DDMandateContractAkkaAdapter()
  implicit lazy val creditorPort : CreditorPort = new CreditorFakeAdapter()
  implicit lazy val ddMandateRepositoryPort : DDMandateRepositoryPort = FakeDDMandateRepositoryAdapterSingleton

  //Implicit X Contract
  implicit lazy val ddMandatePort : DDMandatePort = new DDMandateAkkaAdapter()
  implicit lazy val contractRepositoryPort : ContractRepositoryPort =  FakeContractRepositoryAdapterSingleton
  implicit lazy val fileRepositoryPort : FileDocumentPort = new FileDocumentAkkaAdapter

  //Implicit X FileDocument
  implicit lazy val pdfMakerPort: FileBodyPort = new FileBodyPdfBoxAdapter()
  implicit lazy val templateRepository: FileDocumentTemplateRepositoryPort = new FileDocumentTemplateRepositoryLocalAdapter()
  implicit lazy val fileDocumentRepository: FileDocumentRepositoryPort = new FileDocumentRepositoryLocalAdapter()

  lazy val logger = Logging(actorSystem, classOf[App])

//val ref = context.actorOf(SomeActor.props(someLong)), "actor")
  actorSystem.actorOf(ContractUIActor.props(),name = "contractActor")
  actorSystem.actorOf(DDMandateUIActor.props(),name = "ddMandateActor")
  actorSystem.actorOf(FileDocumentUIActorAdapter.props(),name = "fileDocumentActor")


  val host: String = "127.0.0.1"
  val port: Int = Try(System.getenv("PORT")).map(_.toInt).getOrElse(9000)


  val ddMandateRoutes = new DDMandateUIRoutes()
  val contractRoutes = new ContractUIRoutes()
  val fileDocumentRoutes = new FileDocumentUIHttpAdapter()

  lazy val routes: Route = handleExceptions(globalExceptionHandler){
    pathPrefix("api") {
      concat(
        contractRoutes.routes,
        ddMandateRoutes.route,
        fileDocumentRoutes.routes
      )
    }
  }

  def startServer() = {

    Http().bindAndHandle(routes, host, port)
    logger.info(s"Starting the HTTP server at ${port}")
    Await.result(actorSystem.whenTerminated, Duration.Inf)
  }
}