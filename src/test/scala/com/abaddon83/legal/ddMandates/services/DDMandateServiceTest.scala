package com.abaddon83.legal.ddMandates.services

import java.util.UUID

import com.abaddon83.legal.ddMandates.adapters.CreditorAdapters.fake.FakeCreditorAdapter
import com.abaddon83.legal.ddMandates.adapters.bankAccountAdapters.fake.FakeBankAccountAdapter
import com.abaddon83.legal.ddMandates.adapters.contractDDMandateAdapters.fake.FakeContractDDMandateAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateRepositoryAdapters.fake.FakeDDMandateRepositoryAdapter
import com.abaddon83.legal.ddMandates.domainModels._
import com.abaddon83.legal.ddMandates.ports.{BankAccountPort, CreditorPort, ContractDDMandatePort, DDMandateRepositoryPort}
import com.abaddon83.legal.sharedValueObjects.bankAccounts.BankAccountIdentity
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity
import com.abaddon83.legal.utilities.{DDMandateDomainElementHelper, UUIDRegistryHelper}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite





class DDMandateServiceTest extends AnyFunSuite with ScalaFutures with DDMandateDomainElementHelper  {
   protected val ddMandateRepository: DDMandateRepositoryPort = new FakeDDMandateRepositoryAdapter
   protected val bankAccountPort: BankAccountPort = new FakeBankAccountAdapter()
   protected val creditorPort: CreditorPort = new FakeCreditorAdapter()
   protected  val contractPort: ContractDDMandatePort = new FakeContractDDMandateAdapter()
   protected val ddMandateService: DDMandateService =   new DDMandateService(ddMandateRepository,bankAccountPort,creditorPort, contractPort)




  test("Create DD Mandate with a contract not signed") {

    val bankAccountId = BankAccountIdentity(UUID.fromString("146a525d-402b-4bce-a317-3f00d05aede0"))



    val mandateCreatedIdentity=ddMandateService.createDDMandate(bankAccountId,"IT1").futureValue.identity

    val ddMandate = ddMandateRepository.findDDMandateNotAcceptedById(mandateCreatedIdentity).futureValue
    assert(ddMandate.debtor.bankAccount.identity == bankAccountId)
    assert(ddMandate.contract.isSigned==false)
    assert(ddMandate.isInstanceOf[DDMandateNotAccepted])
    assert(ddMandate.identity == mandateCreatedIdentity)

    UUIDRegistryHelper.add("ddmandate",ddMandate.identity.uuid,"not_accepted_not_signed")
  }

  test("Create a DD Mandate with a bank account that already has a DD mandate associated") {
    val ddMandateuuid =UUIDRegistryHelper.search("ddmandate","not_accepted_not_signed").get
    val ddmandateIdentity = DDMandateIdentity(ddMandateuuid)

    val ddMandateExist =  ddMandateRepository.findDDMandateNotAcceptedById(ddmandateIdentity).futureValue
    val bankAccountIdentity = ddMandateExist.debtor.bankAccount.identity

    assert(ddMandateService.createDDMandate(bankAccountIdentity,"IT1").failed.futureValue.isInstanceOf[java.lang.IllegalArgumentException])


  }

  test("accept dd mandate"){

    val ddMandateuuid =UUIDRegistryHelper.search("ddmandate","not_accepted_not_signed").get
    val ddmandateIdentity = DDMandateIdentity(ddMandateuuid)

    assert(ddMandateService.acceptDDMandate(ddmandateIdentity).failed.futureValue.isInstanceOf[NoSuchElementException])


    val ddMandateNotAccepted =ddMandateRepository.findDDMandateNotAcceptedById(ddmandateIdentity).futureValue

    //sign the Contract
    contractPort.asInstanceOf[FakeContractDDMandateAdapter].setSigned(ddMandateNotAccepted.contract.identity)
    //validate the BankAccount
    bankAccountPort.asInstanceOf[FakeBankAccountAdapter].acceptBankAccount(ddMandateNotAccepted.debtor.bankAccount.identity)

    ddMandateService.acceptDDMandate(ddmandateIdentity).futureValue

    val ddMandateAccepted = ddMandateRepository.findDDMandateAcceptedById(ddmandateIdentity).futureValue
    assert(ddMandateAccepted.contract.isSigned == true)
    assert(ddMandateAccepted.debtor.bankAccount.isValid)
    assert(ddMandateAccepted.isInstanceOf[DDMandateAccepted])

    UUIDRegistryHelper.update("ddmandate",ddMandateAccepted.identity.value,"accepted")

  }

  test("cancel dd mandate active"){

    val ddMandateuuid =UUIDRegistryHelper.search("ddmandate","accepted").get
    val ddmandateIdentity = DDMandateIdentity(ddMandateuuid)

    ddMandateService.cancelDDMandate(ddmandateIdentity).futureValue


    val ddMandateCancelled = ddMandateRepository.findDDMandateCancelledById(ddmandateIdentity).futureValue

    assert(ddMandateCancelled.identity == ddmandateIdentity)

    UUIDRegistryHelper.update("ddmandate",ddMandateCancelled.identity.value,"cencelled")

  }

  test("Create DD Mandate with a debtor with a canceled DD mandate") {

    val ddMandateuuid =UUIDRegistryHelper.search("ddmandate","cencelled").get
    val ddmandateIdentity = DDMandateIdentity(ddMandateuuid)

    val ddMandateCancelled = ddMandateRepository.findDDMandateCancelledById(ddmandateIdentity).futureValue

    val bankAccountIdentity = ddMandateCancelled.debtor.bankAccount.identity

    val newDDMandateIdentity = ddMandateService.createDDMandate(bankAccountIdentity,"IT1").futureValue.identity

    val ddMandate = ddMandateRepository.findDDMandateNotAcceptedById(newDDMandateIdentity).futureValue
    assert(ddMandate.debtor.bankAccount.identity == bankAccountIdentity)
    assert(ddMandate.isInstanceOf[DDMandateNotAccepted])
    assert(ddMandate.identity == newDDMandateIdentity)
  }
  
}
