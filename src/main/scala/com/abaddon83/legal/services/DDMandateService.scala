package com.abaddon83.legal.services

import java.util.{Date, UUID}

import com.abaddon83.legal.domainModel.contract.{Contract, DD_MANDATE, GIA_AGREEMENT, ISA_AGREEMENT, PIP_AGREEMENT, SIPP_AGREEMENT}
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.{BankAccountIdentity, EUBankAccount}
import com.abaddon83.legal.domainModel.ddMandates.{ACCEPTED, CANCELED, Creditor, DDMandateAccepted, DDMandateCanceled, DDMandateDraft, DDMandateIdentity, DDMandateNotAccepted, DRAFT, Debtor, LegalEntityCode, NOACCEPTED}
import com.abaddon83.legal.ports.{BankAccountPort, ContractRepositoryPort, CreditorPort, DDMandateRepositoryPort, FileRepositoryPort}

class DDMandateService(
                        repositoryPort: DDMandateRepositoryPort,
                        bankAccountPort: BankAccountPort,
                        creditorPort: CreditorPort
                        ) {

  def newDraftDDMandate(bankAccountId: BankAccountIdentity, legalEntity: LegalEntityCode): DDMandateDraft = {
    val debtor = bankAccountPort.findDebtorByBankAccountId(bankAccountId) match {
      case Some(value) =>value
      case None => throw new NoSuchElementException("Debtor with bank account id: "++bankAccountId.toString++" not found")
    }

    val creditor = creditorPort.findByLegalEntity(legalEntity) match {
      case Some(value) =>value
      case None =>  throw new NoSuchElementException("Creditor with legalEntity: "++legalEntity.toString++" not found")
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
      case ddMandate if ddMandate.status == NOACCEPTED => ddMandate
      case ddMandate if ddMandate.status == ACCEPTED => ddMandate
    }.size == 0
  }

  def updateContractSigned(contract: Contract): DDMandateNotAccepted = {
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

  def acceptDDMandate(ddMandateIdentity:  DDMandateIdentity): DDMandateAccepted = {
    val ddMandateNotAccepted = repositoryPort.findDDMandateNotAcceptedById(ddMandateIdentity) match {
      case Some(value) => value
      case None => throw new NoSuchElementException(s"DD Mandate with id: ${ddMandateIdentity.toString} not found ")
    }
    val DDMandateAccepted = ddMandateNotAccepted.accept()

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
