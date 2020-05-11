package com.abaddon83.legal.ddMandates.services

import com.abaddon83.legal.ddMandates.domainModels._
import com.abaddon83.legal.ddMandates.ports.{BankAccountPort, ContractPort, CreditorPort, DDMandateRepositoryPort}
import com.abaddon83.legal.sharedValueObjects.bankAccounts.BankAccountIdentity
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity

class DDMandateService(
                        repositoryPort: DDMandateRepositoryPort,
                        bankAccountPort: BankAccountPort,
                        creditorPort: CreditorPort,
                        contractPort: ContractPort
                        ) {

  def createDDMandate(bankAccountId: BankAccountIdentity, legalEntity: String): DDMandateNotAccepted = {

    val debtor = bankAccountPort.findDebtorByBankAccountId(bankAccountId) match {
        case Some(value) =>value
        case None => throw new NoSuchElementException(s"Debtor with bank account id: ${bankAccountId} not found")
    }

    if (bankAccountHasOtherMandates(bankAccountId)){
      throw new IllegalArgumentException(s"The bank account ${bankAccountId.toString} has already a mandate associated ")
    }

    val creditor = creditorPort.findByLegalEntity(legalEntity) match {
      case Some(value) =>value
      case None =>  throw new NoSuchElementException(s"Creditor with legalEntity: ${legalEntity} not found")
    }

    val ddMandateDraft = DDMandateDraft(debtor, creditor)

    val contract = contractPort.createContract(ddMandateDraft) match {
      case Some(value) => value
      case None => throw new RuntimeException("Contract not received")
    }

    val ddMandateNotAccepted = ddMandateDraft.assignContract(contract)

    repositoryPort.save(ddMandateNotAccepted)


  }

  def bankAccountHasOtherMandates(bankAccountId: BankAccountIdentity): Boolean = {
    repositoryPort.findAllDDMandatesByBankAccount(bankAccountId).collect{
      case ddMandate if ddMandate.isInstanceOf[DDMandateAccepted] => ddMandate
      case ddMandate if ddMandate.isInstanceOf[DDMandateNotAccepted] => ddMandate
    }.isEmpty match {
      case true => false
      case false => true
    }
  }

  /*def updateContractSigned(contract: ContractSigned): DDMandateNotAccepted = {
    val ddMandateIdentity = DDMandateIdentity(UUID.fromString(contract.reference))

    val ddMandateNotAccepted = repositoryPort.findDDMandateNotAcceptedById(ddMandateIdentity) match {
      case Some(value) => value
      case None => throw new NoSuchElementException(s"DD Mandate with id: ${ddMandateIdentity.toString} not found ")
    }
    val ddMandateNotAcceptedUpdated=ddMandateNotAccepted.updateContractSigned(contract)

    repositoryPort.save(ddMandateNotAcceptedUpdated)
  }*/

  /*def updateBankAccount(ddMandateIdentity:  DDMandateIdentity, bankAccountIdentity: BankAccountIdentity): DDMandateNotAccepted = {

    val ddMandateNotAccepted = repositoryPort.findDDMandateNotAcceptedById(ddMandateIdentity) match {
      case Some(value) => value
      case None => throw new NoSuchElementException(s"DD Mandate with id: ${ddMandateIdentity.toString} not found ")
    }

    val debtor = bankAccountPort.findDebtorByBankAccountId(bankAccountIdentity) match {
      case Some(value) =>value
      case None => throw new IllegalArgumentException("Debtor with bank account id: "++bankAccountIdentity.toString++" not found")
    }
    val ddMandateNotAcceptedUpdated=ddMandateNotAccepted.updateDebtorValidated(debtor)

    repositoryPort.save(ddMandateNotAcceptedUpdated)

  }*/

  def search(): DDMandateRepositoryPort = {
    this.repositoryPort
  }

  def acceptDDMandate(ddMandateIdentity:  DDMandateIdentity): DDMandateAccepted = {
    //get DDmandateNotAccepted
    val ddMandateNotAccepted = repositoryPort.findDDMandateNotAcceptedById(ddMandateIdentity) match {
      case Some(value) => value
      case None => throw new NoSuchElementException(s"DD Mandate with id: ${ddMandateIdentity.toString} not found ")
    }

    //get Debtor with BankAccountValidated
    val bankAccountIdentity = ddMandateNotAccepted.debtor.bankAccount.identity
    val validatedDebtor = bankAccountPort.findValidatedDebtorByBankAccountId(bankAccountIdentity) match {
      case Some(value) =>value
      case None => throw new NoSuchElementException(s"Validated Debtor with bank account id: ${bankAccountIdentity.toString} not found")
    }

    //get signed Contract
    val contractIdentity = ddMandateNotAccepted.contract.identity
    val contractSigned = contractPort.findSignedContractByContractId(contractIdentity) match {
      case Some(value) =>value
      case None => throw new NoSuchElementException(s"Contract signed with id: ${contractIdentity.toString} not found")
    }

    val DDMandateAccepted = ddMandateNotAccepted.accept(contractSigned,validatedDebtor)

    repositoryPort.save(DDMandateAccepted)

  }

  def cancelDDMandate(ddMandateIdentity:  DDMandateIdentity): DDMandateCanceled =
  {
    val ddMandateAccepted = repositoryPort.findDDMandateAcceptedById(ddMandateIdentity) match {
      case Some(value) => value
      case None => throw new NoSuchElementException(s"DD Mandate with id: ${ddMandateIdentity.toString} not found ")
    }
    val ddMandateCanceled = ddMandateAccepted.cancel()
    repositoryPort.save(ddMandateCanceled)
  }

}
