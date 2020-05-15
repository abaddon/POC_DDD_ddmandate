
package com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters

import java.util.UUID

import com.abaddon83.legal.ddMandates.adapters.CreditorAdapters.fake.FakeCreditorAdapter
import com.abaddon83.legal.ddMandates.adapters.bankAccountAdapters.fake.FakeBankAccountAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp.DDMandateAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateContractAdapters.fake.FakeDDMandateContractAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateRepositoryAdapters.fake.FakeDDMandateRepositoryAdapter
import com.abaddon83.legal.ddMandates.ports.{BankAccountPort, CreditorPort, DDMandateContractPort, DDMandateRepositoryPort}
import com.abaddon83.legal.utilities.UUIDRegistryHelper
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import wvlet.airframe.newDesign


class DDMandateAdapterTest extends AnyFunSuite with Matchers with ScalaFutures {

  val session = newDesign
      .bind[BankAccountPort].toInstance(new FakeBankAccountAdapter())
      .bind[DDMandateContractPort].toInstance(new FakeDDMandateContractAdapter() )
      .bind[CreditorPort].toInstance(new FakeCreditorAdapter() )
      .bind[DDMandateRepositoryPort].toInstance(new FakeDDMandateRepositoryAdapter())
      .newSession

  val ddMandateAdapter = session.build[DDMandateAdapter]


  test("create a new mandate no signed"){
    val bankAccountIdString= "146a525d-402b-4bce-a317-3f00d05aede0"
    val legalEntity = "IT1"
    val bankAccountUUID = UUID.fromString(bankAccountIdString)

    whenReady(ddMandateAdapter.createDDMandate(bankAccountUUID,legalEntity)){ ddMandateNotAccepted =>
      assert(ddMandateNotAccepted.debtor.bankAccount.identity.uuid.toString == bankAccountIdString)
      assert(ddMandateNotAccepted.creditor.legalEntityCode == legalEntity)
      assert(ddMandateNotAccepted.contract.isSigned == false)
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
    val ddmandate = ddMandateAdapter.createDDMandate(bankAccountUUID, legalEntity).futureValue

    UUIDRegistryHelper.add("ddMandate_adapter",ddmandate.identity.uuid,"not_accepted")

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

  test("accept a DD mandate with a contract not signed"){
    val ddmandateUUID = UUIDRegistryHelper.search("ddMandate_adapter","not_accepted").get

    assert(ddMandateAdapter.acceptDDMandate(ddmandateUUID).failed.futureValue.isInstanceOf[NoSuchElementException])

  }

}