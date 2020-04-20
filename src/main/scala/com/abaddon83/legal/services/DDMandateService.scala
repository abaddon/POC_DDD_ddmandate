package com.abaddon83.legal.services

import java.util.UUID

import com.abaddon83.legal.domainModel.ddMandates.{Creditor, DDMandate, DDMandateIdentity, Debtor}
import com.abaddon83.legal.ports.{ContractPort, CreditorPort, DDMandateRepositoryPort, BankAccountPort}

class DDMandateService(
                        repositoryPort: DDMandateRepositoryPort,
                        bankAccountPort: BankAccountPort,
                        creditorPort: CreditorPort,
                        contractPort: ContractPort) {

   def createDDMandate(bankAccountId: UUID, legalEntity: String): DDMandate =
   {
     val debtor : Debtor = bankAccountPort.findDebtorByBankAccountId(bankAccountId)
     val creditor: Creditor = creditorPort.findByLegalEntity(legalEntity)

     //TODO - ADD [INV]: only one DD mandate for Creditor and Debitor (excluded cancelled)

     val ddMandate = DDMandate(debtor, creditor)
     val contact = contractPort.createDDMandateContract(ddMandate)

     ddMandate.assignContract(contact)
     repositoryPort.save(ddMandate)


  }

  def acceptDDMandate(mandateIdentity:  DDMandateIdentity): DDMandate =
  {
    val ddMandate = repositoryPort.findDDMandateById(mandateIdentity)
    val bankAccount= bankAccountPort.findBankAccountByBankAccountId(ddMandate.debtor.bankAccount.identifier)
    ddMandate.accept(bankAccount)
    repositoryPort.save(ddMandate)

  }

  def cancelDDMandate(mandateIdentity:  DDMandateIdentity): DDMandate =
  {
    val ddMandate = repositoryPort.findDDMandateById(mandateIdentity)
    ddMandate.cancel()
    repositoryPort.save(ddMandate)

  }



}
