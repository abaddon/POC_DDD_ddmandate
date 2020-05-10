package com.abaddon83.legal.services

import java.util.UUID

import com.abaddon83.legal.domainModel.contract.{Contract, ContractSigned}
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.BankAccountIdentity
import com.abaddon83.legal.domainModel.ddMandates._
import com.abaddon83.legal.ports.{BankAccountPort, CreditorPort, DDMandateRepositoryPort}

class DDMandateService(
                        repositoryPort: DDMandateRepositoryPort,
                        bankAccountPort: BankAccountPort,
                        creditorPort: CreditorPort
                        ) {

  def newDraftDDMandate(bankAccountId: BankAccountIdentity, legalEntity: String): DDMandateDraft = {
    val debtor = bankAccountPort.findDebtorByBankAccountId(bankAccountId) match {
      case Some(value) =>value
      case None => throw new NoSuchElementException("Debtor with bank account id: "++bankAccountId.toString++" not found")
    }

    val creditor = creditorPort.findByLegalEntity(legalEntity) match {
      case Some(value) =>value
      case None =>  throw new NoSuchElementException("Creditor with legalEntity: "++legalEntity++" not found")
    }
    DDMandateDraft(debtor, creditor)
  }

  def createDDMandate(ddMandateDraft : DDMandateDraft, contract: Contract): DDMandateNotAccepted = {

    val DDMandateNotAccepted =bankAccountSuitable(ddMandateDraft.debtor.bankAccount.identity) match {
      case false => throw new IllegalArgumentException(s"The bank account ${ddMandateDraft.debtor.bankAccount.toString} has already a mandate associated ")
      case true => ddMandateDraft.assignContract(contract)
    }
     repositoryPort.save(DDMandateNotAccepted)

  }

  //TO FIX wrong name...
  def bankAccountSuitable(bankAccountId: BankAccountIdentity): Boolean = {
    repositoryPort.findAllDDMandatesByBankAccount(bankAccountId).collect{
      case ddMandate if ddMandate.isInstanceOf[DDMandateAccepted] => ddMandate
      case ddMandate if ddMandate.isInstanceOf[DDMandateNotAccepted] => ddMandate
    }.size == 0
  }

  def updateContractSigned(contract: ContractSigned): DDMandateNotAccepted = {
    val ddMandateIdentity = DDMandateIdentity(UUID.fromString(contract.reference))

    val ddMandateNotAccepted = repositoryPort.findDDMandateNotAcceptedById(ddMandateIdentity) match {
      case Some(value) => value
      case None => throw new NoSuchElementException(s"DD Mandate with id: ${ddMandateIdentity.toString} not found ")
    }
    val ddMandateNotAcceptedUpdated=ddMandateNotAccepted.updateContractSigned(contract)

    repositoryPort.save(ddMandateNotAcceptedUpdated)
  }

  def updateBankAccount(ddMandateIdentity:  DDMandateIdentity, bankAccountIdentity: BankAccountIdentity): DDMandateNotAccepted = {

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

  }

  def search(): DDMandateRepositoryPort = {
    this.repositoryPort
  }

  def acceptDDMandate(ddMandateIdentity:  DDMandateIdentity, contractSigned: ContractSigned): DDMandateAccepted = {
    //get DDmandateNotAccepted
    val ddMandateNotAccepted = repositoryPort.findDDMandateNotAcceptedById(ddMandateIdentity) match {
      case Some(value) => value
      case None => throw new NoSuchElementException(s"DD Mandate with id: ${ddMandateIdentity.toString} not found ")
    }

    //getDebtor with BankAccountValidated
    val bankAccountIdentity = ddMandateNotAccepted.debtor.bankAccount.identity
    val validatedDebtor = bankAccountPort.findValidatedDebtorByBankAccountId(bankAccountIdentity) match {
      case Some(value) =>value
      case None => throw new NoSuchElementException("Validated Debtor with bank account id: "++bankAccountIdentity.toString++" not found")
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
