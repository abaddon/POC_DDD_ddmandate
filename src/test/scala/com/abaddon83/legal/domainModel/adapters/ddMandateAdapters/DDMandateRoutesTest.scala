package com.abaddon83.legal.domainModel.adapters.ddMandateAdapters

import java.util.{Date, UUID}

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.abaddon83.legal.adapters.BankAccountAdapters.Fake.FakeBankAccountAdapter
import com.abaddon83.legal.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapter
import com.abaddon83.legal.adapters.CreditorAdapters.Fake.FakeCreditorAdapter
import com.abaddon83.legal.adapters.DDMandateRepositoryAdapters.Fake.FakeDDMandateRepositoryAdapter
import com.abaddon83.legal.adapters.FileRepositoryAdapters.Fake.FakeFileRepositoryAdapter
import com.abaddon83.legal.adapters.ddMandateAdapters.AkkaHttp.{DDMandateAdapter, DDMandateRoutes}
import com.abaddon83.legal.adapters.ddMandateAdapters.AkkaHttp.messages.{CreateDDMandate, DDMandateJsonSupport, ViewDDMandate}
import com.abaddon83.legal.ports._
import com.abaddon83.legal.services.{ContractService, DDMandateService}
import com.abaddon83.shared.akkaHttp.messages.ErrorMessage
import org.scalatest.concurrent.Eventually
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers


class DDMandateRoutesTest extends AnyFunSuite with Matchers with ScalatestRouteTest with Eventually with DDMandateJsonSupport {
  val ddMandateRepository: DDMandateRepositoryPort = new FakeDDMandateRepositoryAdapter()
  val bankAccountPort: BankAccountPort = new FakeBankAccountAdapter()
  val creditorPort: CreditorPort = new FakeCreditorAdapter()
  val ddMandateService: DDMandateService =   new DDMandateService(ddMandateRepository,bankAccountPort,creditorPort)
  val contractRepository: ContractRepositoryPort = new FakeContractRepositoryAdapter();
  val fileRepository: FileRepositoryPort = new FakeFileRepositoryAdapter()
  val contractService: ContractService = new ContractService(contractRepository, fileRepository)
  val ddMandateAdapter: DDMandateAdapter = new DDMandateAdapter(ddMandateService,contractService)
  val ddMandateRoutes = new DDMandateRoutes(ddMandateAdapter)


  test("Example get has to return Hello world"){
    Get("/ddmandates") ~> ddMandateRoutes.getRoute() ~> check{
      eventually{
        status shouldBe OK
        responseAs[String] shouldEqual "Hello world"
      }
    }
  }

  test("Create a new DD Mandate"){

    val bankAccountUUID = UUID.fromString("146a525d-402b-4bce-a317-3f00d05aede0")
    val legalEntity = "IT1"
    val createDDMandate = new CreateDDMandate(bankAccountUUID,legalEntity)
    Post("/ddmandates",createDDMandate) ~> ddMandateRoutes.getRoute() ~> check{
      eventually{
        status shouldBe OK
        val viewDDMandate = responseAs[ViewDDMandate]
        assert(viewDDMandate.creditor.legalEntityCode == legalEntity)
        assert(viewDDMandate.debtor.bankAccountId.toString == bankAccountUUID.toString)
        assert(viewDDMandate.id.isInstanceOf[UUID])
        assert(viewDDMandate.creationDate.isInstanceOf[Date])
        assert(viewDDMandate.ddMandateType == "Financial")
      }
    }
  }

  test("Create a new DD Mandate with the bank account not available"){

    val bankAccountUUID = UUID.fromString("146a525d-402b-4bce-a317-3f00d05aede1")
    val legalEntity = "IT1"
    val createDDMandate = new CreateDDMandate(bankAccountUUID,legalEntity)
    Post("/ddmandates",createDDMandate) ~> ddMandateRoutes.getRoute() ~> check{
      eventually{
        status shouldBe BadRequest
        val viewDDMandate = responseAs[ErrorMessage]
        assert(viewDDMandate.errorCode == 0)
        assert(viewDDMandate.exceptionType == "java.util.NoSuchElementException")
        assert(viewDDMandate.instance == "/ddmandates")
        assert(viewDDMandate.message == "Debtor with bank account id: BankAccount-146a525d-402b-4bce-a317-3f00d05aede1 not found")
      }
    }
  }

  test("Create a new DD Mandate with the legal entity not available"){

    val bankAccountUUID = UUID.fromString("146a525d-402b-4bce-a317-3f00d05aede0")
    val legalEntity = "NOEXIST"
    val createDDMandate = new CreateDDMandate(bankAccountUUID,legalEntity)
    Post("/ddmandates",createDDMandate) ~> ddMandateRoutes.getRoute() ~> check{
      eventually{
        status shouldBe BadRequest
        val viewDDMandate = responseAs[ErrorMessage]
        assert(viewDDMandate.errorCode == 0)
        assert(viewDDMandate.exceptionType == "java.util.NoSuchElementException")
        assert(viewDDMandate.instance == "/ddmandates")
        assert(viewDDMandate.message == s"Creditor with legalEntity: ${legalEntity} not found")
      }
    }
  }

  test("Create a new DD Mandate with a mal formatted UUID"){

    val bankAccountUUID = "146a525d"
    val legalEntity = "IT1"

    Post("/ddmandates",HttpEntity(ContentTypes.`application/json`, s"""{ "bankAccountId": "${bankAccountUUID}", "legalEntity": "IT5"}""")) ~> Route.seal(ddMandateRoutes.getRoute()) ~> check{

        status shouldBe BadRequest
        val message = responseAs[ErrorMessage]
        assert(message.errorCode == 0)
        assert(message.exceptionType == "spray.json.DeserializationException")
        assert(message.instance == "/")
        assert(message.message == s"Expected UUID format, got ${bankAccountUUID}")
    }
  }
}
