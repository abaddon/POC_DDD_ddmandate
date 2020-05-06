package com.abaddon83.legal.adapters.ddMandateAdapters.AkkaHttp

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.abaddon83.legal.adapters.BankAccountAdapters.Fake.FakeBankAccountAdapter
import com.abaddon83.legal.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapter
import com.abaddon83.legal.adapters.CreditorAdapters.Fake.FakeCreditorAdapter
import com.abaddon83.legal.adapters.DDMandateRepositoryAdapters.Fake.FakeDDMandateRepositoryAdapter
import com.abaddon83.legal.adapters.FileRepositoryAdapters.Fake.FakeFileRepositoryAdapter
import com.abaddon83.legal.ports._
import com.abaddon83.legal.services.{ContractService, DDMandateService}
import com.abaddon83.shared.akkaHttp.AkkaHttpServer
import com.abaddon83.shared.akkaHttp.routes.RoutesBuilder

case class AkkaHttpServerDDMandate() extends AkkaHttpServer{

  override implicit val actorSystem: ActorSystem = ActorSystem(name = "DDMandate")
  override implicit val materializer: ActorMaterializer = ActorMaterializer()

  private lazy val ddMandateRepository: DDMandateRepositoryPort = new FakeDDMandateRepositoryAdapter()
  private lazy val bankAccountPort: BankAccountPort = new FakeBankAccountAdapter()
  private lazy val creditorPort: CreditorPort = new FakeCreditorAdapter()
  private lazy val ddMandateService: DDMandateService =   new DDMandateService(ddMandateRepository,bankAccountPort,creditorPort)
  private lazy val contractRepository: ContractRepositoryPort = new FakeContractRepositoryAdapter();
  private lazy val fileRepository: FileRepositoryPort = new FakeFileRepositoryAdapter()
  private lazy val contractService: ContractService = new ContractService(contractRepository, fileRepository)

  private lazy val ddMandateAdapter = new DDMandateAdapter(ddMandateService,contractService)

  lazy override val apiRoutes: RoutesBuilder = new RoutesBuilder(List(
    new DDMandateRoutes(ddMandateAdapter).getRoute()
  ))

}
