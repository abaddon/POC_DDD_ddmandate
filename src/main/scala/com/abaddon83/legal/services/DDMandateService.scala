package com.abaddon83.legal.services

import java.util.UUID

import com.abaddon83.legal.domainModel.ddMandates.{Creditor, DDMandateAccepted, DDMandateCanceled, DDMandateDraft, DDMandateIdentity, DDMandateNotAccepted, Debtor}
import com.abaddon83.legal.ports.{BankAccountPort, ContractPort, CreditorPort, DDMandateRepositoryPort}

class DDMandateService(
                        repositoryPort: DDMandateRepositoryPort,
                        bankAccountPort: BankAccountPort,
                        creditorPort: CreditorPort,
                        contractPort: ContractPort) {

   def createDDMandate(bankAccountId: UUID, legalEntity: String): DDMandateNotAccepted =
   {
     val debtor : Debtor = bankAccountPort.findDebtorByBankAccountId(bankAccountId)
     val creditor: Creditor = creditorPort.findByLegalEntity(legalEntity)

     val ddMandateDraft = DDMandateDraft(debtor, creditor)
     val contact = contractPort.createDDMandateContract(ddMandateDraft)

     val DDMandateNotAccepted = ddMandateDraft.assignContract(contact)
     repositoryPort.save(DDMandateNotAccepted)

  }

  def acceptDDMandate(mandateIdentity:  DDMandateIdentity): DDMandateAccepted =
  {
    val ddMandateNotAccepted = repositoryPort.findDDMandateNotAcceptedById(mandateIdentity)
    val DDMandateAccepted = ddMandateNotAccepted.accept()

    repositoryPort.save(DDMandateAccepted)

  }

  def cancelDDMandate(mandateIdentity:  DDMandateIdentity): DDMandateCanceled =
  {
    val ddMandateAccepted = repositoryPort.findDDMandateAcceptedById(mandateIdentity)
    val ddMandateCanceled = ddMandateAccepted.cancel()
    repositoryPort.save(ddMandateCanceled)
  }



}
