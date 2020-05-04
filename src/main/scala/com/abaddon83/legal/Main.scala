package com.abaddon83.legal

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import com.abaddon83.legal.adapters.BankAccountAdapters.FakeBankAccountAdapter
import com.abaddon83.legal.adapters.ContractRepositoryAdapters.FakeContractRepositoryAdapter
import com.abaddon83.legal.adapters.CreditorAdapters.FakeCreditorAdapter
import com.abaddon83.legal.adapters.DDMandateRepositoryAdapters.FakeDDMandateRepositoryAdapter
import com.abaddon83.legal.adapters.FileRepositoryAdapters.FakeFileRepositoryAdapter
import com.abaddon83.legal.adapters.ddMandateAdapters.{DDMandateAdapter, DDMandateRoutes}
import com.abaddon83.legal.ports.{BankAccountPort, ContractRepositoryPort, CreditorPort, DDMandateRepositoryPort, FileRepositoryPort}
import com.abaddon83.legal.services.{ContractService, DDMandateService}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Try

object Main extends App {

  val host = "127.0.0.1"
  val port = Try(System.getenv("PORT")).map(_.toInt).getOrElse(9000)

  implicit val actorSystem: ActorSystem = ActorSystem(name = "DDMandate")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  lazy val logger = Logging(actorSystem, classOf[App])


  private val ddMandateRepository: DDMandateRepositoryPort = new FakeDDMandateRepositoryAdapter()
  private val bankAccountPort: BankAccountPort = new FakeBankAccountAdapter()
  private val creditorPort: CreditorPort = new FakeCreditorAdapter()
  private val ddMandateService: DDMandateService =   new DDMandateService(ddMandateRepository,bankAccountPort,creditorPort)
  private val contractRepository: ContractRepositoryPort = new FakeContractRepositoryAdapter();
  private val fileRepository: FileRepositoryPort = new FakeFileRepositoryAdapter()
  private val contractService: ContractService = new ContractService(contractRepository, fileRepository)

  val ddMandateAdapter = new DDMandateAdapter(ddMandateService,contractService)
  val ddMandateRoutes =  DDMandateRoutes(ddMandateAdapter)


  lazy val apiRoutes: Route = pathPrefix("api") {

    ddMandateRoutes.route
  }

  logger.info(s"parameters: ${host}:${port}")

  Http().bindAndHandle(apiRoutes, host, port)
  logger.info(s"Starting the HTTP server at ${port}")
  Await.result(actorSystem.whenTerminated, Duration.Inf)

}
