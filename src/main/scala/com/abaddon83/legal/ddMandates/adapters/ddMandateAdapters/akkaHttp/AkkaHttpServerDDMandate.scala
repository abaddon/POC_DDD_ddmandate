package com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.abaddon83.legal.contracts.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.FileRepositoryAdapters.Fake.FakeFileRepositoryAdapter
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, FileRepositoryPort}
import com.abaddon83.legal.ddMandates.adapters.CreditorAdapters.fake.FakeCreditorAdapter
import com.abaddon83.legal.ddMandates.adapters.bankAccountAdapters.fake.FakeBankAccountAdapter
import com.abaddon83.legal.ddMandates.adapters.contractAdapters.fake.FakeContractAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateRepositoryAdapters.fake.FakeDDMandateRepositoryAdapter
import com.abaddon83.legal.ddMandates.ports.{BankAccountPort, ContractPort, CreditorPort, DDMandateRepositoryPort}
import com.abaddon83.legal.ddMandates.services.DDMandateService
import com.abaddon83.libs.akkaHttp.AkkaHttpServer
import com.abaddon83.libs.akkaHttp.routes.RoutesBuilder

case class AkkaHttpServerDDMandate() extends AkkaHttpServer{

  override implicit val actorSystem: ActorSystem = ActorSystem(name = "DDMandate")
  override implicit val materializer: ActorMaterializer = ActorMaterializer()

  private lazy val ddMandateRepository: DDMandateRepositoryPort = new FakeDDMandateRepositoryAdapter()
  private lazy val bankAccountPort: BankAccountPort = new FakeBankAccountAdapter()
  private lazy val creditorPort: CreditorPort = new FakeCreditorAdapter()
  private lazy val contractPort: ContractPort = new FakeContractAdapter()
  private lazy val ddMandateService: DDMandateService =   new DDMandateService(ddMandateRepository,bankAccountPort,creditorPort,contractPort)
  private lazy val contractRepository: ContractRepositoryPort = new FakeContractRepositoryAdapter();
  private lazy val fileRepository: FileRepositoryPort = new FakeFileRepositoryAdapter()

  private lazy val ddMandateAdapter = new DDMandateAdapter(ddMandateService)

  lazy override val apiRoutes: RoutesBuilder = new RoutesBuilder(
    List(
      new DDMandateRoutes(ddMandateAdapter).getRoute()
    )
  )

}
