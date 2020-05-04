package com.abaddon83.legal.services

import java.util.{Date, UUID}

import com.abaddon83.legal.adapters.BankAccountAdapters.FakeBankAccountAdapter
import com.abaddon83.legal.adapters.CreditorAdapters.FakeCreditorAdapter
import com.abaddon83.legal.adapters.DDMandateRepositoryAdapters.FakeDDMandateRepositoryAdapter
import com.abaddon83.legal.domainModel.contract.{ContractSigned, ContractUnSigned}
import com.abaddon83.legal.domainModel.ddMandates.{DDMandateAccepted, DDMandateCanceled, DDMandateDraft, DDMandateNotAccepted, Financial}
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.BankAccountIdentity
import com.abaddon83.legal.ports.{BankAccountPort, CreditorPort, DDMandateRepositoryPort}
import com.abaddon83.legal.tests.utilities.{DDMandateServiceTestHelper, DomainElementHelper}
import org.scalatest.funsuite.AnyFunSuite

class DDMandateServiceTest extends AnyFunSuite with DDMandateServiceTestHelper with DomainElementHelper {
  override protected val ddMandateRepository: DDMandateRepositoryPort = new FakeDDMandateRepositoryAdapter()
  override protected val bankAccountPort: BankAccountPort = new FakeBankAccountAdapter()
  override protected val creditorPort: CreditorPort = new FakeCreditorAdapter()
  override protected val ddMandateService: DDMandateService =   new DDMandateService(ddMandateRepository,bankAccountPort,creditorPort)

  test("New DD draft mandate with a bank account wrong") {

    val bankAccountId = BankAccountIdentity(UUID.fromString("8ee0b59a-8e7d-46c0-a058-8cfcd1d92204"))
    val legalEntity = "IT1"

    assertThrows[NoSuchElementException] {
      ddMandateService.newDraftDDMandate(bankAccountId,legalEntity)
    }
  }

  test("New DD draft mandate with an existing bank account") {

    val bankAccountId = BankAccountIdentity(UUID.fromString("146a525d-402b-4bce-a317-3f00d05aede0"))
    val legalEntity = "IT1"
    val ddMandate = ddMandateService.newDraftDDMandate(bankAccountId,legalEntity)

    assert(ddMandate.creditor.legalEntityCode == legalEntity)
    assert(ddMandate.debtor.bankAccount.identity == bankAccountId)
    assert(ddMandate.ddMandateType == Financial)
    assert(ddMandate.isInstanceOf[DDMandateDraft])
  }

  test("Create DD Mandate with a contract not signed") {

    val bankAccountId = BankAccountIdentity(UUID.fromString("146a525d-402b-4bce-a317-3f00d05aede0"))
    val ddMandateDraft = buildDraftDDMandate(bankAccountId)

    assert(ddMandateRepository.findDDMandateNotAcceptedById(ddMandateDraft.identity).isEmpty)

    val unsignedContract = buildContract(ddMandateDraft,false)
    ddMandateService.createDDMandate(ddMandateDraft,unsignedContract)

    val ddMandate = ddMandateRepository.findDDMandateNotAcceptedById(ddMandateDraft.identity)
    assert(ddMandate.isDefined)
    assert(ddMandate.get.debtor == ddMandateDraft.debtor)
    assert(ddMandate.get.contract.isInstanceOf[ContractUnSigned])
    assert(ddMandate.get.contract == unsignedContract)
    assert(ddMandate.get.isInstanceOf[DDMandateNotAccepted])
    assert(ddMandate.get.identity == ddMandateDraft.identity)
    assert(ddMandate.get.creditor == ddMandateDraft.creditor)
  }

  test("Create a DD Mandate with a bank account that already has a DD mandate associated") {

    val bankAccountId = BankAccountIdentity(UUID.fromString("146a525d-402b-4bce-a317-3f00d05aede0"))
    val ddMandateDraft = buildDraftDDMandate(bankAccountId)

    assert(ddMandateRepository.findDDMandateNotAcceptedById(ddMandateDraft.identity).isEmpty)

    val unsignedContract = buildContract(ddMandateDraft,false)

    assertThrows[java.lang.IllegalArgumentException] {
      ddMandateService.createDDMandate(ddMandateDraft,unsignedContract)
    }

  }

  test("Create DD Mandate with a contract signed") {

    val bankAccountId = BankAccountIdentity(UUID.fromString("4a943d91-1ed4-4a1d-904e-9ec830106299"))
    val ddMandateDraft = buildDraftDDMandate(bankAccountId)

    assert(ddMandateRepository.findDDMandateNotAcceptedById(ddMandateDraft.identity).isEmpty)

    val unsignedContract = buildContract(ddMandateDraft,true)
    ddMandateService.createDDMandate(ddMandateDraft,unsignedContract)

    val ddMandate = ddMandateRepository.findDDMandateNotAcceptedById(ddMandateDraft.identity)
    assert(ddMandate.isDefined)
    assert(ddMandate.get.debtor == ddMandateDraft.debtor)
    assert(ddMandate.get.contract.isInstanceOf[ContractSigned])
    assert(ddMandate.get.contract == unsignedContract)
    assert(ddMandate.get.isInstanceOf[DDMandateNotAccepted])
    assert(ddMandate.get.identity == ddMandateDraft.identity)
    assert(ddMandate.get.creditor == ddMandateDraft.creditor)
  }

  test("update dd mandate not accepted with a signed contract") {

    val bankAccountId = BankAccountIdentity(UUID.fromString("d4456de3-bcb0-4009-adff-803d7884c647"))
    val ddMandateDraft = buildDraftDDMandate(bankAccountId)

    val unsignedContract = buildContract(ddMandateDraft,false).asInstanceOf[ContractUnSigned]
    ddMandateService.createDDMandate(ddMandateDraft,unsignedContract)
    val signedContract = unsignedContract.sign(fakeFileRepository,new Date())

    ddMandateService.updateContractSigned(signedContract)

    val ddMandate = ddMandateRepository.findDDMandateNotAcceptedById(ddMandateDraft.identity)
    assert(ddMandate.get.contract.isInstanceOf[ContractSigned])
    assert(ddMandate.get.contract == signedContract)
    assert(ddMandate.get.isInstanceOf[DDMandateNotAccepted])
  }

  test("update dd mandate not accepted with a debtor validated") {

    val bankAccountId = BankAccountIdentity(UUID.fromString("c9671ce9-a148-4ee0-9bae-8672fb9fbaa1"))
    val ddMandateDraft = buildDraftDDMandate(bankAccountId)

    val unsignedContract = buildContract(ddMandateDraft,false).asInstanceOf[ContractUnSigned]
    ddMandateService.createDDMandate(ddMandateDraft,unsignedContract)
    assert(!ddMandateDraft.debtor.bankAccount.isValid)

    val backAccountAdapter = bankAccountPort.asInstanceOf[FakeBankAccountAdapter]
    backAccountAdapter.acceptBankAccount(bankAccountId)

    ddMandateService.updateBankAccount(ddMandateDraft.identity,bankAccountId)

    val ddMandate = ddMandateRepository.findDDMandateNotAcceptedById(ddMandateDraft.identity)
    assert(ddMandate.get.debtor.bankAccount.isValid)
    assert(ddMandate.get.isInstanceOf[DDMandateNotAccepted])
  }

  test("accept dd mandate"){
    val bankAccountId = BankAccountIdentity(UUID.fromString("c9671ce9-a148-4ee0-9bae-8672fb9fbaa1"))
    val ddMandate = ddMandateRepository.findAllDDMandatesByBankAccount(bankAccountId).last

    assertThrows[java.lang.AssertionError] {
      ddMandateService.acceptDDMandate(ddMandate.identity)
    }

    val contractSigned = ddMandate.asInstanceOf[DDMandateNotAccepted].contract.asInstanceOf[ContractUnSigned].sign(fakeFileRepository,new Date())

    ddMandateService.updateContractSigned(contractSigned)

    ddMandateService.acceptDDMandate(ddMandate.identity)

    val ddMandateAccepted = ddMandateRepository.findDDMandateAcceptedById(ddMandate.identity)
    assert(ddMandateAccepted.get.contract.isInstanceOf[ContractSigned])
    assert(ddMandateAccepted.get.debtor.bankAccount.isValid)
    assert(ddMandateAccepted.get.isInstanceOf[DDMandateAccepted])

  }

  test("cancel dd mandate active"){
    val bankAccountId = BankAccountIdentity(UUID.fromString("c9671ce9-a148-4ee0-9bae-8672fb9fbaa1"))
    val ddMandate = ddMandateRepository.findAllDDMandatesByBankAccount(bankAccountId).last

    ddMandateService.cancelDDMandate(ddMandate.identity)

    val ddMandateCancelled = ddMandateRepository.findAllDDMandatesByBankAccount(bankAccountId).last

    assert(ddMandateCancelled.isInstanceOf[DDMandateCanceled])
  }

  test("Create DD Mandate with a debtor with a canceled DD mandate") {

    val bankAccountId = BankAccountIdentity(UUID.fromString("c9671ce9-a148-4ee0-9bae-8672fb9fbaa1"))
    val ddMandateDraft = buildDraftDDMandate(bankAccountId)

    assert(ddMandateRepository.findDDMandateNotAcceptedById(ddMandateDraft.identity).isEmpty)

    assert(ddMandateService.bankAccountSuitable(bankAccountId))

    val unsignedContract = buildContract(ddMandateDraft,false)
    ddMandateService.createDDMandate(ddMandateDraft,unsignedContract)

    val ddMandate = ddMandateRepository.findDDMandateNotAcceptedById(ddMandateDraft.identity)
    assert(ddMandate.isDefined)
    assert(ddMandate.get.debtor == ddMandateDraft.debtor)
    assert(ddMandate.get.contract.isInstanceOf[ContractUnSigned])
    assert(ddMandate.get.contract == unsignedContract)
    assert(ddMandate.get.isInstanceOf[DDMandateNotAccepted])
    assert(ddMandate.get.identity == ddMandateDraft.identity)
    assert(ddMandate.get.creditor == ddMandateDraft.creditor)
  }
  
}
