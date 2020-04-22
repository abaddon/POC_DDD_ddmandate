package com.abaddon83.legal.Application

import java.util.UUID

import com.abaddon83.legal.domainModel.ddMandates.{DDMandateNotAccepted, LegalEntityCode}
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.BankAccountIdentity
import com.abaddon83.legal.services.{ContractService, DDMandateService}

class DDMandateApplication(
               ddMandateService: DDMandateService,
               contractService: ContractService
               ) {

  def createDDMandate(bankAccountId: UUID, legalEntityCode: LegalEntityCode): DDMandateNotAccepted ={

    val draftDDMandate = ddMandateService.newDraftDDMandate(BankAccountIdentity(bankAccountId),legalEntityCode)

    val contact = contractService.createDDMandateContract(draftDDMandate)

    ddMandateService.createDDMandate(draftDDMandate,contact)
  }
}
