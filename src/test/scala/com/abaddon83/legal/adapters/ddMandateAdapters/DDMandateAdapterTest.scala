
package com.abaddon83.legal.adapters.ddMandateAdapters

import java.util.UUID

import com.abaddon83.legal.adapters.BankAccountAdapters.Fake.FakeBankAccountAdapter
import com.abaddon83.legal.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapter
import com.abaddon83.legal.adapters.CreditorAdapters.Fake.FakeCreditorAdapter
import com.abaddon83.legal.adapters.DDMandateRepositoryAdapters.Fake.FakeDDMandateRepositoryAdapter
import com.abaddon83.legal.adapters.FileRepositoryAdapters.Fake.FakeFileRepositoryAdapter
import com.abaddon83.legal.adapters.ddMandateAdapters.AkkaHttp.DDMandateAdapter
import com.abaddon83.legal.domainModel.contract.ContractUnSigned
import com.abaddon83.legal.ports._
import com.abaddon83.legal.services.{ContractService, DDMandateService}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Await
import scala.concurrent.duration._

class DDMandateAdapterTest extends AnyFunSuite with Matchers with ScalaFutures {

  val ddMandateRepository: DDMandateRepositoryPort = new FakeDDMandateRepositoryAdapter()
  val bankAccountPort: BankAccountPort = new FakeBankAccountAdapter()
  val creditorPort: CreditorPort = new FakeCreditorAdapter()
  val ddMandateService: DDMandateService =   new DDMandateService(ddMandateRepository,bankAccountPort,creditorPort)
  val contractRepository: ContractRepositoryPort = new FakeContractRepositoryAdapter();
  val fileRepository: FileRepositoryPort = new FakeFileRepositoryAdapter()
  val contractService: ContractService = new ContractService(contractRepository, fileRepository)

  val ddMandateAdapter: DDMandatePort = new DDMandateAdapter(ddMandateService,contractService)

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
    val bankAccountIdString= "146a525d-402b-4bce-a317-3f00d05aede0"
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
