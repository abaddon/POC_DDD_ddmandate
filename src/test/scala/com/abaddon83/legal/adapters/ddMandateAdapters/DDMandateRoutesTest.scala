package com.abaddon83.legal.adapters.ddMandateAdapters

import java.util.{Date, UUID}

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.abaddon83.legal.adapters.BankAccountAdapters.Fake.FakeBankAccountAdapter
import com.abaddon83.legal.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapter
import com.abaddon83.legal.adapters.CreditorAdapters.Fake.FakeCreditorAdapter
import com.abaddon83.legal.adapters.DDMandateRepositoryAdapters.Fake.FakeDDMandateRepositoryAdapter
import com.abaddon83.legal.adapters.FileRepositoryAdapters.Fake.FakeFileRepositoryAdapter
import com.abaddon83.legal.adapters.ddMandateAdapters.AkkaHttp.messages.{CreateDDMandate, DDMandateJsonSupport, RestViewDDMandate}
import com.abaddon83.legal.adapters.ddMandateAdapters.AkkaHttp.{DDMandateAdapter, DDMandateRoutes}
import com.abaddon83.legal.ports._
import com.abaddon83.legal.services.{ContractService, DDMandateService}
import com.abaddon83.shared.akkaHttp.messages.ErrorMessage
import org.scalatest.concurrent.Eventually
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable.ListBuffer


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

  var ddmandateNotAcceptedList: ListBuffer[UUID] = new ListBuffer[UUID]
  var ddmandateAcceptedList: ListBuffer[UUID] = new ListBuffer[UUID]
  var ddmandateCancelledList: ListBuffer[UUID] = new ListBuffer[UUID]

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

        ddmandateNotAcceptedList.addOne(viewDDMandate.id)
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
        assert(message.instance == "/MalformedRequestContentRejection")
        assert(message.message == s"Expected UUID format, got ${bankAccountUUID}")
    }
  }

  test("Get a DD mandate") {
    //val legalEntity = "IT1"
    //val bankAccountUUID = UUID.fromString("4a943d91-1ed4-4a1d-904e-9ec830106299")
    //val ddmandate = Await.result(ddMandateAdapter.createDDMandate(bankAccountUUID, legalEntity), 1.seconds)
    val ddMandateUUIDString = ddmandateNotAcceptedList.findLast(_ =>true).get.toString


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
        assert(message.instance == "/ddmandates")
        assert(message.message == s"DD Mandate with id: ${ddMandateUUIDString} not found ")
      }
    }
  }

  test("try to accept a DD Mandate not accepted with a contract not signed"){
    val ddMandateUUIDString = ddmandateNotAcceptedList.findLast(_ =>true).get.toString

    // /ddmandates/[uuid]/activate-command/[uuid-command]
    Put("/ddmandates/${ddMandateUUIDString}\",HttpEntity(ContentTypes.`application/json`, s"""{ "bankAccountId": "${bankAccountUUID}", "legalEntity": "IT5"}""")) ~> Route.seal(ddMandateRoutes.getRoute()) ~> check{

      status shouldBe BadRequest
      val message = responseAs[ErrorMessage]
      assert(message.errorCode == 0)
      assert(message.exceptionType == "spray.json.DeserializationException")
      assert(message.instance == "/MalformedRequestContentRejection")
      assert(message.message == s"Expected UUID format, got ${bankAccountUUID}")
    }


  }


  private def debug(message: ErrorMessage){
    println(message.errorCode)
    println(message.exceptionType)
    println(message.instance)
    println(message.message)
  }
}
