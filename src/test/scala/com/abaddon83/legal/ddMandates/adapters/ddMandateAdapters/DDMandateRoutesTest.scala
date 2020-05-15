package com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters

import java.util.{Date, UUID}

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.abaddon83.legal.ddMandates.adapters.CreditorAdapters.fake.FakeCreditorAdapter
import com.abaddon83.legal.ddMandates.adapters.bankAccountAdapters.fake.FakeBankAccountAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateContractAdapters.fake.FakeDDMandateContractAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp.DDMandateRoutes
import com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp.messages.{CreateDDMandateRequest, DDMandateJsonSupport, RestViewDDMandate}
import com.abaddon83.legal.ddMandates.adapters.ddMandateRepositoryAdapters.fake.FakeDDMandateRepositoryAdapter
import com.abaddon83.legal.ddMandates.ports.{BankAccountPort, CreditorPort, DDMandateContractPort, DDMandateRepositoryPort}
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity
import com.abaddon83.legal.utilities.UUIDRegistryHelper
import com.abaddon83.libs.akkaHttp.messages.ErrorMessage
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import wvlet.airframe._


class DDMandateRoutesTest extends AnyFunSuite with ScalaFutures with Matchers with ScalatestRouteTest with Eventually with DDMandateJsonSupport{

val session = newDesign
  .bind[BankAccountPort].toInstance(new FakeBankAccountAdapter())
  .bind[DDMandateContractPort].toInstance(new FakeDDMandateContractAdapter() )
  .bind[CreditorPort].toInstance(new FakeCreditorAdapter() )
  .bind[DDMandateRepositoryPort].toInstance(new FakeDDMandateRepositoryAdapter() )
  .newSession

  val routes = session.build[DDMandateRoutes]

  test("Create a new DD Mandate"){

    val bankAccountUUID = UUID.fromString("146a525d-402b-4bce-a317-3f00d05aede0")
    val legalEntity = "IT1"
    val createDDMandate = CreateDDMandateRequest(bankAccountUUID,legalEntity)
    Post("/ddmandates",createDDMandate) ~> routes.ddMandateRoutes ~> check{
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
    val createDDMandate = new CreateDDMandateRequest(bankAccountUUID,legalEntity)
    Post("/ddmandates",createDDMandate) ~> routes.ddMandateRoutes ~> check{
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
    val createDDMandate = CreateDDMandateRequest(bankAccountUUID,legalEntity)
    Post("/ddmandates",createDDMandate) ~> routes.ddMandateRoutes ~> check{
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

    Post("/ddmandates",HttpEntity(ContentTypes.`application/json`, s"""{ "bankAccountId": "${bankAccountUUID}", "legalEntity": "IT5"}""")) ~> Route.seal(routes.ddMandateRoutes) ~> check{

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

    Get(s"/ddmandates/${ddMandateUUIDString}") ~> routes.ddMandateRoutes ~> check {
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

    Get(s"/ddmandates/${ddMandateUUIDString}") ~> Route.seal(routes.ddMandateRoutes) ~> check{
      //eventually{
        val message = responseAs[ErrorMessage]

        assert(status == NotFound)
        assert(message.errorCode == 0)
        assert(message.exceptionType == "java.util.NoSuchElementException")
        assert(message.instance == s"/ddmandates/${ddMandateUUIDString}")
        assert(message.message == s"DDMandate with id: ${DDMandateIdentity(UUID.fromString(ddMandateUUIDString))} not found")
     // }
    }
  }


  test("activate a DD Mandate not accepted with bank account valid and contract not signed") {

    val ddMandateUUIDString= UUIDRegistryHelper.search("ddMandate","not_accepted").get.toString

    val ddMandateNotAccepted = routes.ddMandateRepositoryePort.findDDMandateNotAcceptedById(DDMandateIdentity(UUID.fromString(ddMandateUUIDString))).futureValue
    //routes.contractPort.asInstanceOf[FakeDDMandateContractAdapter].setSigned(ddMandateNotAccepted.contract.identity)
    routes.bankAccountPort.asInstanceOf[FakeBankAccountAdapter].acceptBankAccount(ddMandateNotAccepted.debtor.bankAccount.identity)

    Put(s"/ddmandates/${ddMandateUUIDString}/activate") ~> Route.seal(routes.ddMandateRoutes) ~> check {
      //println(response)
      val message = responseAs[ErrorMessage]
      //println(message)
      //debug(message)
      status shouldBe BadRequest
      assert(message.errorCode == 0)
      assert(message.exceptionType == "java.util.NoSuchElementException")
      assert(message.instance == s"/ddmandates/${ddMandateUUIDString}/activate")
      assert(message.message == s"Contract signed with id: ${ddMandateNotAccepted.contract.identity} not found")
    }
  }

  test("activate a DD Mandate not accepted with bank account not valid and contract signed") {

    val ddMandateUUIDString= UUIDRegistryHelper.search("ddMandate","not_accepted").get.toString

    val ddMandateNotAccepted = routes.ddMandateRepositoryePort.findDDMandateNotAcceptedById(DDMandateIdentity(UUID.fromString(ddMandateUUIDString))).futureValue
    routes.contractPort.asInstanceOf[FakeDDMandateContractAdapter].setSigned(ddMandateNotAccepted.contract.identity)
    routes.bankAccountPort.asInstanceOf[FakeBankAccountAdapter].rejectBankAccount(ddMandateNotAccepted.debtor.bankAccount.identity)

    Put(s"/ddmandates/${ddMandateUUIDString}/activate") ~> Route.seal(routes.ddMandateRoutes) ~> check {
      //println(response)
      val message = responseAs[ErrorMessage]
      //println(message)
      //debug(message)
      status shouldBe BadRequest
      assert(message.errorCode == 0)
      assert(message.exceptionType == "java.util.NoSuchElementException")
      assert(message.instance == s"/ddmandates/${ddMandateUUIDString}/activate")

      assert(message.message == s"Debtor with bank account id: ${ddMandateNotAccepted.debtor.bankAccount.identity} is not validated")
    }
  }

  test("activate a DD Mandate not accepted with a contract signed") {

    val ddMandateUUIDString= UUIDRegistryHelper.search("ddMandate","not_accepted").get.toString

    val ddMandateNotAccepted = routes.ddMandateRepositoryePort.findDDMandateNotAcceptedById(DDMandateIdentity(UUID.fromString(ddMandateUUIDString))).futureValue
    routes.contractPort.asInstanceOf[FakeDDMandateContractAdapter].setSigned(ddMandateNotAccepted.contract.identity)
    routes.bankAccountPort.asInstanceOf[FakeBankAccountAdapter].acceptBankAccount(ddMandateNotAccepted.debtor.bankAccount.identity)


    Put(s"/ddmandates/${ddMandateUUIDString}/activate") ~> Route.seal(routes.ddMandateRoutes) ~> check {
      val message = responseAs[RestViewDDMandate]

      status shouldBe OK
      assert(message.id.toString == ddMandateUUIDString)
      assert(message.status == "Accepted")
      UUIDRegistryHelper.update("ddMandate",message.id,"accepted")
    }
  }

  test("Get a DD mandate accepted") {

    val ddMandateUUIDString= UUIDRegistryHelper.search("ddMandate","accepted").get.toString

    Get(s"/ddmandates/${ddMandateUUIDString}") ~> routes.ddMandateRoutes ~> check {
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

    Put(s"/ddmandates/${ddMandateUUIDString}/activate") ~> Route.seal(routes.ddMandateRoutes) ~> check {
      val message = responseAs[ErrorMessage]

      status shouldBe BadRequest
      assert(message.errorCode == 0)
      assert(message.exceptionType == "java.util.NoSuchElementException")
      assert(message.instance == s"/ddmandates/${ddMandateUUIDString}/activate")
      assert(message.message == s"DDMandateNotAccepted with id: ${DDMandateIdentity(UUID.fromString(ddMandateUUIDString))} not found")
    }
  }

  test("cancel a DD Mandate ") {

    val ddMandateUUIDString= UUIDRegistryHelper.search("ddMandate","accepted").get.toString

    Put(s"/ddmandates/${ddMandateUUIDString}/cancel") ~> Route.seal(routes.ddMandateRoutes) ~> check {
      val message = responseAs[RestViewDDMandate]

      status shouldBe OK
      assert(message.id.toString == ddMandateUUIDString)
      assert(message.status == "Canceled")
      UUIDRegistryHelper.update("ddMandate",message.id,"canceled")
    }
  }

  test("Get a DD mandate canceled") {

    val ddMandateUUIDString= UUIDRegistryHelper.search("ddMandate","canceled").get.toString

    Get(s"/ddmandates/${ddMandateUUIDString}") ~> routes.ddMandateRoutes ~> check {
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

    Put(s"/ddmandates/${ddMandateUUIDString}/cancel") ~> Route.seal(routes.ddMandateRoutes) ~> check {

      val message = responseAs[ErrorMessage]

      status shouldBe BadRequest
      assert(message.errorCode == 0)
      assert(message.exceptionType == "java.util.NoSuchElementException")
      assert(message.instance == s"/ddmandates/${ddMandateUUIDString}/cancel")
      assert(message.message == s"DDMandateAccepted with id: ${DDMandateIdentity(UUID.fromString(ddMandateUUIDString))} not found")
    }
  }

  private def debug(message: ErrorMessage){
    println(message.errorCode)
    println(message.exceptionType)
    println(message.instance)
    println(message.message)
  }
}