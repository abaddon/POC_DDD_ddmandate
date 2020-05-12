package com.abaddon83.legal.ddMandates.services

import java.util.UUID

import com.abaddon83.legal.ddMandates.adapters.CreditorAdapters.fake.FakeCreditorAdapter
import com.abaddon83.legal.ddMandates.adapters.bankAccountAdapters.fake.FakeBankAccountAdapter
import com.abaddon83.legal.ddMandates.adapters.contractAdapters.fake.FakeContractAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateRepositoryAdapters.fake.FakeDDMandateRepositoryAdapter
import com.abaddon83.legal.ddMandates.domainModels._
import com.abaddon83.legal.ddMandates.ports.{BankAccountPort, ContractPort, CreditorPort, DDMandateRepositoryPort}
import com.abaddon83.legal.sharedValueObjects.bankAccounts.BankAccountIdentity
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity
import com.abaddon83.legal.utilities.{DDMandateDomainElementHelper, UUIDRegistryHelper}
import org.scalatest.funsuite.AnyFunSuite






class DDMandateServiceTest extends AnyFunSuite with DDMandateDomainElementHelper  {
   protected val ddMandateRepository: DDMandateRepositoryPort = new FakeDDMandateRepositoryAdapter()
   protected val bankAccountPort: BankAccountPort = new FakeBankAccountAdapter()
   protected val creditorPort: CreditorPort = new FakeCreditorAdapter()
   protected  val contractPort: ContractPort = new FakeContractAdapter()
   protected val ddMandateService: DDMandateService =   new DDMandateService(ddMandateRepository,bankAccountPort,creditorPort, contractPort)




  test("Create DD Mandate with a contract not signed") {

    val bankAccountId = BankAccountIdentity(UUID.fromString("146a525d-402b-4bce-a317-3f00d05aede0"))



    val ddMandateIdentity = ddMandateService.createDDMandate(bankAccountId,"IT1").identity

    val ddMandate = ddMandateRepository.findDDMandateNotAcceptedById(ddMandateIdentity)
    assert(ddMandate.isDefined)
    assert(ddMandate.get.debtor.bankAccount.identity == bankAccountId)
    assert(ddMandate.get.contract.isInstanceOf[ContractUnSigned])
    assert(ddMandate.get.isInstanceOf[DDMandateNotAccepted])
    assert(ddMandate.get.identity == ddMandateIdentity)

    UUIDRegistryHelper.add("ddmandate",ddMandate.get.identity.uuid,"not_accepted_not_signed")
  }

  test("Create a DD Mandate with a bank account that already has a DD mandate associated") {

    val ddMandateuuid =UUIDRegistryHelper.search("ddmandate","not_accepted_not_signed").get
    val ddmandateIdentity = DDMandateIdentity(ddMandateuuid)

    val ddMandateExist =  ddMandateRepository.findDDMandateNotAcceptedById(ddmandateIdentity).get
    val bankAccountIdentity = ddMandateExist.debtor.bankAccount.identity


    assertThrows[java.lang.IllegalArgumentException] {
      ddMandateService.createDDMandate(bankAccountIdentity,"IT1")
    }

  }

  test("accept dd mandate"){

    val ddMandateuuid =UUIDRegistryHelper.search("ddmandate","not_accepted_not_signed").get
    val ddmandateIdentity = DDMandateIdentity(ddMandateuuid)
    assertThrows[NoSuchElementException] {
      ddMandateService.acceptDDMandate(ddmandateIdentity)
    }

    val ddMandateNotAccepted =ddMandateRepository.findDDMandateNotAcceptedById(ddmandateIdentity).get

    //sign the Contract
    contractPort.asInstanceOf[FakeContractAdapter].setSigned(ddMandateNotAccepted.contract.identity)
    //validate the BankAccount
    bankAccountPort.asInstanceOf[FakeBankAccountAdapter].acceptBankAccount(ddMandateNotAccepted.debtor.bankAccount.identity)

    ddMandateService.acceptDDMandate(ddmandateIdentity)

    val ddMandateAccepted = ddMandateRepository.findDDMandateAcceptedById(ddmandateIdentity)
    assert(ddMandateAccepted.get.contract.isInstanceOf[ContractSigned])
    assert(ddMandateAccepted.get.debtor.bankAccount.isValid)
    assert(ddMandateAccepted.get.isInstanceOf[DDMandateAccepted])

    UUIDRegistryHelper.update("ddmandate",ddMandateAccepted.get.identity.value,"accepted")

  }

  test("cancel dd mandate active"){

    val ddMandateuuid =UUIDRegistryHelper.search("ddmandate","accepted").get
    val ddmandateIdentity = DDMandateIdentity(ddMandateuuid)

    ddMandateService.cancelDDMandate(ddmandateIdentity)


    val ddMandateCancelled = ddMandateRepository.findDDMandateCancelledById(ddmandateIdentity)

    assert(ddMandateCancelled.get.identity == ddmandateIdentity)

    UUIDRegistryHelper.update("ddmandate",ddMandateCancelled.get.identity.value,"cencelled")

  }

  test("Create DD Mandate with a debtor with a canceled DD mandate") {

    val ddMandateuuid =UUIDRegistryHelper.search("ddmandate","cencelled").get
    val ddmandateIdentity = DDMandateIdentity(ddMandateuuid)

    val ddMandateCancelled = ddMandateRepository.findDDMandateCancelledById(ddmandateIdentity)

    val bankAccountIdentity = ddMandateCancelled.get.debtor.bankAccount.identity

    val newDDMandateIdentity = ddMandateService.createDDMandate(bankAccountIdentity,"IT1").identity


    val ddMandate = ddMandateRepository.findDDMandateNotAcceptedById(newDDMandateIdentity)
    assert(ddMandate.isDefined)
    assert(ddMandate.get.debtor.bankAccount.identity == bankAccountIdentity)
    assert(ddMandate.get.isInstanceOf[DDMandateNotAccepted])
    assert(ddMandate.get.identity == newDDMandateIdentity)

  }
  
}
