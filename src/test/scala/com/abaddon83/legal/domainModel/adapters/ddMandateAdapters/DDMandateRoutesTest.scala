package com.abaddon83.legal.domainModel.adapters.ddMandateAdapters

import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.http.scaladsl.server._
import Directives._
import akka.actor.ActorSystem

import scala.concurrent.duration._
import akka.http.scaladsl.model.StatusCodes._
import akka.testkit._
import com.abaddon83.legal.adapters.BankAccountAdapters.FakeBankAccountAdapter
import com.abaddon83.legal.adapters.ContractRepositoryAdapters.FakeContractRepositoryAdapter
import com.abaddon83.legal.adapters.CreditorAdapters.FakeCreditorAdapter
import com.abaddon83.legal.adapters.DDMandateRepositoryAdapters.FakeDDMandateRepositoryAdapter
import com.abaddon83.legal.adapters.FileRepositoryAdapters.FakeFileRepositoryAdapter
import com.abaddon83.legal.adapters.ddMandateAdapters.{DDMandateAdapter, DDMandateRoutes}
import com.abaddon83.legal.ports.{BankAccountPort, ContractRepositoryPort, CreditorPort, DDMandateRepositoryPort, FileRepositoryPort}
import com.abaddon83.legal.services.{ContractService, DDMandateService}
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Millis, Seconds, Span}


class DDMandateRoutesTest extends WordSpec with Matchers with ScalatestRouteTest with Eventually {

  val ddMandateRepository: DDMandateRepositoryPort = new FakeDDMandateRepositoryAdapter()
  val bankAccountPort: BankAccountPort = new FakeBankAccountAdapter()
  val creditorPort: CreditorPort = new FakeCreditorAdapter()
  val ddMandateService: DDMandateService =   new DDMandateService(ddMandateRepository,bankAccountPort,creditorPort)
  val contractRepository: ContractRepositoryPort = new FakeContractRepositoryAdapter();
  val fileRepository: FileRepositoryPort = new FakeFileRepositoryAdapter()
  val contractService: ContractService = new ContractService(contractRepository, fileRepository)

  val ddMandateAdapter: DDMandateAdapter = new DDMandateAdapter(ddMandateService,contractService)

  val ddMandateRoutes = DDMandateRoutes(ddMandateAdapter)

  implicit override val patienceConfig = PatienceConfig(timeout = scaled(Span(3, Seconds)), interval = scaled(Span(5, Millis)))

  "The service" should {

    "return a greeting for GET requests to the root path" in {
      // tests:

      Get("/api/ddmandates") ~> ddMandateRoutes.route ~> check{
        //eventually{
          status shouldBe OK
          responseAs[String] shouldEqual "Hello world"
        //}


      }
    }
  }

}
