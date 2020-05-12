
package com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters

import java.util.UUID

import com.abaddon83.legal.ddMandates.adapters.CreditorAdapters.fake.FakeCreditorAdapter
import com.abaddon83.legal.ddMandates.adapters.bankAccountAdapters.fake.FakeBankAccountAdapter
import com.abaddon83.legal.ddMandates.adapters.contractAdapters.fake.FakeContractAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp.DDMandateAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateRepositoryAdapters.fake.FakeDDMandateRepositoryAdapter
import com.abaddon83.legal.ddMandates.domainModels.ContractUnSigned
import com.abaddon83.legal.ddMandates.ports._
import com.abaddon83.legal.ddMandates.services.DDMandateService
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Await
import scala.concurrent.duration._

class DDMandateAdapterTest extends AnyFunSuite with Matchers with ScalaFutures {

  val ddMandateRepository: DDMandateRepositoryPort = new FakeDDMandateRepositoryAdapter()
  val bankAccountPort: BankAccountPort = new FakeBankAccountAdapter()
  val creditorPort: CreditorPort = new FakeCreditorAdapter()
  val contractPort: ContractPort = new FakeContractAdapter()
  val ddMandateService: DDMandateService =   new DDMandateService(ddMandateRepository,bankAccountPort,creditorPort,contractPort)


  val ddMandateAdapter: DDMandatePort = new DDMandateAdapter(ddMandateService)

  test("create a new mandate no signed"){
    val bankAccountIdString= "146a525d-402b-4bce-a317-3f00d05aede0"
    val legalEntity = "IT1"
    val bankAccountUUID = UUID.fromString(bankAccountIdString)

    whenReady(ddMandateAdapter.createDDMandate(bankAccountUUID,legalEntity)){ ddMandateNotAccepted =>
      assert(ddMandateNotAccepted.debtor.bankAccount.identity.uuid.toString == bankAccountIdString)
      assert(ddMandateNotAccepted.creditor.legalEntityCode == legalEntity)
      assert(ddMandateNotAccepted.contract.isInstanceOf[ContractUnSigned])
    }
  }

  test("create a new mandate no signed with a wrong bankAccount id"){
    val bankAccountIdString= "146a525d-402b-4bce-a317-3f00d05aede1"
    val legalEntity = "IT1"
    val bankAccountUUID = UUID.fromString(bankAccountIdString)

    ddMandateAdapter.createDDMandate(bankAccountUUID,legalEntity).failed.futureValue shouldBe an [java.util.NoSuchElementException]

  }

  test("create a new mandate no signed with a wrong legalEntityCode"){
    val bankAccountIdString= "d4456de3-bcb0-4009-adff-803d7884c647"
    val legalEntity = "ITwrong"
    val bankAccountUUID = UUID.fromString(bankAccountIdString)

    ddMandateAdapter.createDDMandate(bankAccountUUID,legalEntity).failed.futureValue shouldBe an [java.util.NoSuchElementException]

  }

  test("find a DD mandate"){
    val legalEntity = "IT1"
    val bankAccountUUID = UUID.fromString("4a943d91-1ed4-4a1d-904e-9ec830106299")
    val ddmandate = Await.result(
      ddMandateAdapter.createDDMandate(bankAccountUUID, legalEntity),
      1.seconds)

    val ddMandateUUID = ddmandate.identity.uuid

    whenReady(ddMandateAdapter.findByIdDDMandate(ddMandateUUID)) { ddMandateFound =>
          assert(ddMandateFound.identity.uuid == ddMandateUUID)
          assert(ddMandateFound.debtor.bankAccount.identity.uuid == bankAccountUUID)
          assert(ddMandateFound.creditor.legalEntityCode == legalEntity)
        }
    }

  test("find a DD mandate that doesn't exit"){
    val ddMandateUUID = UUID.fromString("4a943d91-1ed4-4a1d-904e-9ec830106299")

    ddMandateAdapter.findByIdDDMandate(ddMandateUUID).failed.futureValue shouldBe an [java.util.NoSuchElementException]
  }

}
