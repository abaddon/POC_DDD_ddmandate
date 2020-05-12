package com.abaddon83.legal.contracts.adapters.contractAdapters

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.abaddon83.legal.contracts.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.FileRepositoryAdapters.Fake.FakeFileRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.contractAdapters.akkaHttp.messages.{ContractJsonSupport, ContractView, CreateContractRequest}
import com.abaddon83.legal.contracts.adapters.contractAdapters.akkaHttp.{ContractAdapter, ContractRoutes}
import com.abaddon83.legal.contracts.adapters.ddMandateAdapters.fake.FakeDDMandateAdapter
import com.abaddon83.legal.contracts.ports.{ContractPort, ContractRepositoryPort, DDMandatePort, FileRepositoryPort}
import com.abaddon83.legal.contracts.services.ContractService
import com.abaddon83.legal.utilities.UUIDRegistryHelper
import org.scalatest.concurrent.Eventually
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ContractRouterTest extends AnyFunSuite with Matchers with ScalatestRouteTest with Eventually with ContractJsonSupport{
  val ddMandatePort: DDMandatePort = new FakeDDMandateAdapter()
  val fileRepository: FileRepositoryPort = new FakeFileRepositoryAdapter()
  val contractRepository: ContractRepositoryPort = new FakeContractRepositoryAdapter()
  val contractService = new ContractService(contractRepository,fileRepository,ddMandatePort)
  val contractPort: ContractPort = new ContractAdapter(contractService)

  val contractRouters = new ContractRoutes(contractPort)

  test("create a new Contract"){
    val contractType = "DDMANDATE"
    val ddMandateUUIDReference: UUID = UUID.fromString("79abadf2-84db-42bc-81d5-4577778d38af")
    val createContractRequest = CreateContractRequest(contractType,ddMandateUUIDReference)

    Post("/contracts",createContractRequest) ~> Route.seal(contractRouters.getRoute()) ~> check{
      //eventually{
        status shouldBe OK
        val response = responseAs[ContractView]
        assert(response.contractType == "DDMANDATE")
        assert(response.format == "PDF")
        assert(response.reference == ddMandateUUIDReference.toString)

        UUIDRegistryHelper.add("contract",response.id,"not_signed")
      //}

    }

  }


}
