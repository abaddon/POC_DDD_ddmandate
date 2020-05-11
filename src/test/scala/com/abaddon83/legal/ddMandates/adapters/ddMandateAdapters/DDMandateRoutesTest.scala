package com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters

import java.util.{Date, UUID}

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.abaddon83.legal.ddMandates.adapters.CreditorAdapters.fake.FakeCreditorAdapter
import com.abaddon83.legal.ddMandates.adapters.bankAccountAdapters.fake.FakeBankAccountAdapter
import com.abaddon83.legal.ddMandates.adapters.contractAdapters.fake.FakeContractAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp.messages.{CreateDDMandate, DDMandateJsonSupport, RestViewDDMandate}
import com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp.{DDMandateAdapter, DDMandateRoutes}
import com.abaddon83.legal.ddMandates.adapters.ddMandateRepositoryAdapters.fake.FakeDDMandateRepositoryAdapter
import com.abaddon83.legal.ddMandates.ports.{BankAccountPort, ContractPort, CreditorPort, DDMandateRepositoryPort}
import com.abaddon83.legal.ddMandates.services.DDMandateService
import com.abaddon83.legal.ddMandates.utilities.UUIDRegistryHelper
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity
import com.abaddon83.libs.akkaHttp.messages.ErrorMessage
import org.scalatest.concurrent.Eventually
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers


class DDMandateRoutesTest extends AnyFunSuite with Matchers with ScalatestRouteTest with Eventually with DDMandateJsonSupport{
  val ddMandateRepository: DDMandateRepositoryPort = new FakeDDMandateRepositoryAdapter()
  val bankAccountPort: BankAccountPort = new FakeBankAccountAdapter()
  val creditorPort: CreditorPort = new FakeCreditorAdapter()
  val contractPort: ContractPort = new FakeContractAdapter()

  val ddMandateService: DDMandateService =   new DDMandateService(ddMandateRepository,bankAccountPort,creditorPort, contractPort)
  val ddMandateAdapter: DDMandateAdapter = new DDMandateAdapter(ddMandateService)

  val ddMandateRoutes = new DDMandateRoutes(ddMandateAdapter)


  test("Create a new DD Mandate"){

    val bankAccountUUID = UUID.fromString("146a525d-402b-4bce-a317-3f00d05aede0")
    val legalEntity = "IT1"
    val createDDMandate = new CreateDDMandate(bankAccountUUID,legalEntity)
    Post("/ddmandates",createDDMandate) ~> ddMandateRoutes.getRoute() ~> check{
      eventually{
        status shouldBe OK
        val viewDDMandate = responseAs[RestViewDDMandate]
        assert(viewDDMandate.creditor.legalEntityCode == legalEntity)
        assert(viewDDMandate.debtor.bankAccountId.toString == bankAccountUUID.toString)
        assert(viewDDMandate.id.isInstanceOf[UUID])
        assert(viewDDMandate.creationDate.isInstanceOf[Date])
        assert(viewDDMandate.ddMandateType == "Financial")

        UUIDRegistryHelper.add("ddMandate",viewDDMandate.id.asInstanceOf[UUID],"not_accepted")
      }
    }
  }

  test("Create a new DD Mandate with the bank account not available"){

    val bankAccountUUID = UUID.fromString("146a525d-402b-4bce-a317-3f00d05aede1")
    val legalEntity = "IT1"
    val createDDMandate = new CreateDDMandate(bankAccountUUID,legalEntity)
    Post("/ddmandates",createDDMandate) ~> ddMandateRoutes.getRoute() ~> check{
      eventually{
        val message = responseAs[ErrorMessage]
        status shouldBe BadRequest
        assert(message.errorCode == 0)
        assert(message.exceptionType == "java.util.NoSuchElementException")
        assert(message.instance == "/ddmandates")
        assert(message.message == "Debtor with bank account id: BankAccount-146a525d-402b-4bce-a317-3f00d05aede1 not found")
      }
    }
  }

  test("Create a new DD Mandate with the legal entity not available"){

    val bankAccountUUID = UUID.fromString("d4456de3-bcb0-4009-adff-803d7884c647")
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
        assert(message.instance == "/MalformedRequestContentRejection")
        assert(message.message == s"Expected UUID format, got ${bankAccountUUID}")
    }
  }

  test("Get a DD mandate not accepted") {

    val ddMandateUUIDString= UUIDRegistryHelper.search("ddMandate","not_accepted").get.toString

    Get(s"/ddmandates/${ddMandateUUIDString}") ~> ddMandateRoutes.getRoute() ~> check {
      eventually {
        status shouldBe OK
        val viewDDMandate = responseAs[RestViewDDMandate]
        assert(viewDDMandate.id.toString == ddMandateUUIDString)
        assert(viewDDMandate.status == "Not Accepted")
      }
    }
  }

  test("Get a DD mandate that doesn't exist"){

    val ddMandateUUIDString="4a943d91-1ed4-4a1d-904e-9ec830106299"

    Get(s"/ddmandates/${ddMandateUUIDString}") ~> Route.seal(ddMandateRoutes.getRoute()) ~> check{
      eventually{
        val message = responseAs[ErrorMessage]

        assert(status == NotFound)
        assert(message.errorCode == 0)
        assert(message.exceptionType == "java.util.NoSuchElementException")
        assert(message.instance == s"/ddmandates/${ddMandateUUIDString}")
        assert(message.message == s"DD Mandate with id: ${ddMandateUUIDString} not found ")
      }
    }
  }


  test("activate a DD Mandate not accepted with a contract not signed") {

    val ddMandateUUIDString= UUIDRegistryHelper.search("ddMandate","not_accepted").get.toString

    Put(s"/ddmandates/${ddMandateUUIDString}/activate") ~> Route.seal(ddMandateRoutes.getRoute()) ~> check {
      val message = responseAs[ErrorMessage]
      //debug(message)
      status shouldBe BadRequest
      assert(message.errorCode == 0)
      assert(message.exceptionType == "java.util.NoSuchElementException")
      assert(message.instance == s"/ddmandates/${ddMandateUUIDString}/activate")
      assert(message.message == s"Validated Debtor with bank account id: BankAccount-146a525d-402b-4bce-a317-3f00d05aede0 not found")
    }
  }

  test("activate a DD Mandate not accepted with a contract signed") {

    val ddMandateUUIDString= UUIDRegistryHelper.search("ddMandate","not_accepted").get.toString

    val ddMandateNotAccepted = ddMandateRepository.findDDMandateNotAcceptedById(DDMandateIdentity(UUID.fromString(ddMandateUUIDString))).get
    contractPort.asInstanceOf[FakeContractAdapter].setSigned(ddMandateNotAccepted.contract.identity)
    bankAccountPort.asInstanceOf[FakeBankAccountAdapter].acceptBankAccount(ddMandateNotAccepted.debtor.bankAccount.identity)


    Put(s"/ddmandates/${ddMandateUUIDString}/activate") ~> Route.seal(ddMandateRoutes.getRoute()) ~> check {
      val message = responseAs[RestViewDDMandate]

      status shouldBe OK
      assert(message.id.toString == ddMandateUUIDString)
      assert(message.status == "Accepted")
      UUIDRegistryHelper.update("ddMandate",message.id,"accepted")
    }
  }

  test("Get a DD mandate accepted") {

    val ddMandateUUIDString= UUIDRegistryHelper.search("ddMandate","accepted").get.toString

    Get(s"/ddmandates/${ddMandateUUIDString}") ~> ddMandateRoutes.getRoute() ~> check {
      eventually {
        status shouldBe OK
        val viewDDMandate = responseAs[RestViewDDMandate]
        assert(viewDDMandate.id.toString == ddMandateUUIDString)
        assert(viewDDMandate.status == "Accepted")
      }
    }
  }

  test("activate a DD Mandate accepted a second time") {

    val ddMandateUUIDString= UUIDRegistryHelper.search("ddMandate","accepted").get.toString

    Put(s"/ddmandates/${ddMandateUUIDString}/activate") ~> Route.seal(ddMandateRoutes.getRoute()) ~> check {
      val message = responseAs[ErrorMessage]

      status shouldBe BadRequest
      assert(message.errorCode == 0)
      assert(message.exceptionType == "java.lang.ClassCastException")
      assert(message.instance == s"/ddmandates/${ddMandateUUIDString}/activate")
      assert(message.message == s"com.abaddon83.legal.ddMandates.domainModels.DDMandateAccepted cannot be cast to com.abaddon83.legal.ddMandates.domainModels.DDMandateNotAccepted")
    }
  }

  test("cancel a DD Mandate ") {

    val ddMandateUUIDString= UUIDRegistryHelper.search("ddMandate","accepted").get.toString

    Put(s"/ddmandates/${ddMandateUUIDString}/cancel") ~> Route.seal(ddMandateRoutes.getRoute()) ~> check {
      val message = responseAs[RestViewDDMandate]

      status shouldBe OK
      assert(message.id.toString == ddMandateUUIDString)
      assert(message.status == "Canceled")
      UUIDRegistryHelper.update("ddMandate",message.id,"canceled")
    }
  }

  test("Get a DD mandate canceled") {

    val ddMandateUUIDString= UUIDRegistryHelper.search("ddMandate","canceled").get.toString

    Get(s"/ddmandates/${ddMandateUUIDString}") ~> ddMandateRoutes.getRoute() ~> check {
      eventually {
        status shouldBe OK
        val viewDDMandate = responseAs[RestViewDDMandate]
        assert(viewDDMandate.id.toString == ddMandateUUIDString)
        assert(viewDDMandate.status == "Canceled")
      }
    }
  }

  test("cancel a DD Mandate 2 times ") {

    val ddMandateUUIDString= UUIDRegistryHelper.search("ddMandate","canceled").get.toString

    Put(s"/ddmandates/${ddMandateUUIDString}/cancel") ~> Route.seal(ddMandateRoutes.getRoute()) ~> check {

      val message = responseAs[ErrorMessage]

      status shouldBe BadRequest
      assert(message.errorCode == 0)
      assert(message.exceptionType == "java.lang.ClassCastException")
      assert(message.instance == s"/ddmandates/${ddMandateUUIDString}/cancel")
      assert(message.message == s"com.abaddon83.legal.ddMandates.domainModels.DDMandateCanceled cannot be cast to com.abaddon83.legal.ddMandates.domainModels.DDMandateAccepted")
    }
  }

  private def debug(message: ErrorMessage){
    println(message.errorCode)
    println(message.exceptionType)
    println(message.instance)
    println(message.message)
  }
}
