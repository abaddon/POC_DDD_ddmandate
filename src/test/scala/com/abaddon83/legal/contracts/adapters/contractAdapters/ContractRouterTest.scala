package com.abaddon83.legal.contracts.adapters.contractAdapters

import java.util.{Date, UUID}

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.abaddon83.legal.contracts.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.contractUIAdapters.akka.akkHttp.ContractUIRoutes
import com.abaddon83.legal.contracts.adapters.contractUIAdapters.akka.akkHttp.messages.{ContractJsonSupport, ContractView, CreateContractRequest, SignContractRequest}
import com.abaddon83.legal.contracts.adapters.ddMandateAdapters.fake.DDMandateFakeAdapter
import com.abaddon83.legal.contracts.adapters.fileDocumentAdapters.fake.FileDocumentFakeAdapter
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, DDMandatePort, FileDocumentPort}
import com.abaddon83.legal.utilities.UUIDRegistryHelper
import com.abaddon83.libs.akkaHttp.messages.ErrorMessage
import org.scalatest.concurrent.Eventually
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ContractRouterTest extends AnyFunSuite with Matchers with ScalatestRouteTest with Eventually with ContractJsonSupport{

  implicit val ddMandatePort : DDMandatePort = new DDMandateFakeAdapter
  implicit val contractRepositoryPort : ContractRepositoryPort =  new FakeContractRepositoryAdapter
  implicit val fileRepositoryPort : FileDocumentPort = new FileDocumentFakeAdapter

  val contractRoutes = new ContractUIRoutes()

  contractRoutes.ddMandatePort.asInstanceOf[DDMandateFakeAdapter].loadTestData();

  test("create a new Contract"){

    val contractType = "DDMANDATE"
    val ddMandateUUIDReference: UUID = UUID.fromString("79abadf2-84db-42bc-81d5-4577778d38af")
    val createContractRequest = CreateContractRequest(contractType,ddMandateUUIDReference)

    Post("/contracts",createContractRequest) ~> Route.seal(contractRoutes.routes) ~> check{

        status shouldBe OK
        val response = responseAs[ContractView]
        assert(response.contractType == "DDMANDATE")
        assert(response.format == "PDF")
        assert(response.reference == ddMandateUUIDReference.toString)
        assert(response.status == "unsigned")

      UUIDRegistryHelper.add("contract",response.id,"unsigned")

    }

  }

  test("get a Contract"){

    val contractUUID = UUIDRegistryHelper.search("contract","unsigned").get
    val ddMandateUUIDString = "79abadf2-84db-42bc-81d5-4577778d38af"

    Get(s"/contracts/${contractUUID}") ~> Route.seal(contractRoutes.routes) ~> check{

      status shouldBe OK
      val response = responseAs[ContractView]
      assert(response.contractType == "DDMANDATE")
      assert(response.format == "PDF")
      assert(response.reference == ddMandateUUIDString)
      assert(response.status == "unsigned")

    }
  }

  test("sign a  Contract"){
    val contractUUID = UUIDRegistryHelper.search("contract","unsigned").get

    val ddMandateUUIDReference: UUID = UUID.fromString("79abadf2-84db-42bc-81d5-4577778d38af")
    val signatureDate = new Date()
    val signContractRequest = SignContractRequest(signatureDate)


    Put(s"/contracts/${contractUUID}/sign",signContractRequest) ~> Route.seal(contractRoutes.routes) ~> check{

      status shouldBe OK
      val response = responseAs[ContractView]
      assert(response.contractType == "DDMANDATE")
      assert(response.format == "PDF")
      assert(response.reference == ddMandateUUIDReference.toString)
      assert(response.status == "signed")
      assert(response.signatureDate.get.toString == signatureDate.toString)

      UUIDRegistryHelper.update("contract",contractUUID,"signed")
    }

  }

  private def debug(message: ErrorMessage){
    println(message.errorCode)
    println(message.exceptionType)
    println(message.instance)
    println(message.message)
  }


}
