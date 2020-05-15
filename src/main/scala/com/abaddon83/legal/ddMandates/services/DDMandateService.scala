package com.abaddon83.legal.ddMandates.services

import com.abaddon83.legal.ddMandates.domainModels._
import com.abaddon83.legal.ddMandates.ports.{BankAccountPort, CreditorPort, DDMandateContractPort, DDMandateRepositoryPort}
import com.abaddon83.legal.sharedValueObjects.bankAccounts.BankAccountIdentity
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DDMandateService(
                        repositoryPort: DDMandateRepositoryPort,
                        bankAccountPort: BankAccountPort,
                        creditorPort: CreditorPort,
                        contractPort: DDMandateContractPort
                        ) {

  def createDDMandate(bankAccountId: BankAccountIdentity, legalEntity: String): Future[DDMandateNotAccepted] = {
    for{
      debtor <- findValidDebtor(bankAccountId) recoverWith {
        case npe: Exception =>
          Future.failed(npe)
      }
      creditor <- creditorPort.findByLegalEntity(legalEntity)
      ddMandateDraft <- Future(DDMandateDraft(debtor, creditor))
      contract <- contractPort.createContract(ddMandateDraft)
      ddMandateNotAccepted <- Future(ddMandateDraft.assignContract(contract))
    } yield repositoryPort.save(ddMandateNotAccepted)

  }

  def findValidDebtor(bankAccountIdentity: BankAccountIdentity): Future[Debtor] ={
    for{
      debtor <- bankAccountPort.findDebtorByBankAccountId(bankAccountIdentity)
      hasOtherMandates  <- bankAccountHasOtherMandates(bankAccountIdentity)
    } yield hasOtherMandates match {
      case true => throw  new IllegalArgumentException(s"The debtor with bank account id ${bankAccountIdentity.uuid} has already a DD mandate")
      case false => debtor
    }
  }


  def bankAccountHasOtherMandates(bankAccountId: BankAccountIdentity): Future[Boolean] = {
    for {
      list <- repositoryPort.findAllDDMandatesByBankAccount(bankAccountId)

    } yield list.collect{
        case ddMandate if ddMandate.isInstanceOf[DDMandateAccepted] || ddMandate.isInstanceOf[DDMandateNotAccepted]  => ddMandate
      }.size >0

  }


  def search(): DDMandateRepositoryPort = {
    this.repositoryPort
  }

  def acceptDDMandate(ddMandateIdentity:  DDMandateIdentity): Future[DDMandateAccepted] = {
    for{
      ddMandateNotAccepted <- repositoryPort.findDDMandateNotAcceptedById(ddMandateIdentity)
      validatedDebtor <- bankAccountPort.findValidatedDebtorByBankAccountId(ddMandateNotAccepted.debtor.bankAccount.identity)
      contractSigned <- contractPort.findSignedContractByContractId(ddMandateNotAccepted.contract.identity)
      ddMandateAccepted = ddMandateNotAccepted.accept(contractSigned,validatedDebtor)
    } yield repositoryPort.save(ddMandateAccepted)

  }

  def cancelDDMandate(ddMandateIdentity:  DDMandateIdentity): Future[DDMandateCanceled] = {
    for {
      ddMandateAccepted <- repositoryPort.findDDMandateAcceptedById(ddMandateIdentity)
      ddMandateCanceled = ddMandateAccepted.cancel()
    } yield repositoryPort.save(ddMandateCanceled)
  }

}
