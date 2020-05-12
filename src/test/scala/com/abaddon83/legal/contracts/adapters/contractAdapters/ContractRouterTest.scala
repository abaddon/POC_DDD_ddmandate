package com.abaddon83.legal.contracts.adapters.contractAdapters

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.abaddon83.legal.contracts.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.FileRepositoryAdapters.Fake.FakeFileRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.contractAdapters.akkaHttp.ContractRoutes
import com.abaddon83.legal.contracts.adapters.contractAdapters.akkaHttp.messages.{ContractJsonSupport, ContractView, CreateContractRequest}
import com.abaddon83.legal.contracts.adapters.ddMandateAdapters.fake.FakeDDMandateAdapter
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, DDMandatePort, FileRepositoryPort}
import com.abaddon83.legal.utilities.UUIDRegistryHelper
import org.scalatest.concurrent.Eventually
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import wvlet.airframe.newDesign

class ContractRouterTest extends AnyFunSuite with Matchers with ScalatestRouteTest with Eventually with ContractJsonSupport{

  val session = newDesign
    .bind[DDMandatePort].toInstance(new FakeDDMandateAdapter())
    .bind[ContractRepositoryPort].toInstance(new FakeContractRepositoryAdapter() )
    .bind[FileRepositoryPort].toInstance(new FakeFileRepositoryAdapter())
    .newSession

  val routes = session.build[ContractRoutes]

  test("create a new Contract"){
    val contractType = "DDMANDATE"
    val ddMandateUUIDReference: UUID = UUID.fromString("79abadf2-84db-42bc-81d5-4577778d38af")
    val createContractRequest = CreateContractRequest(contractType,ddMandateUUIDReference)

    Post("/contracts",createContractRequest) ~> Route.seal(routes.contractRoutes) ~> check{
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
