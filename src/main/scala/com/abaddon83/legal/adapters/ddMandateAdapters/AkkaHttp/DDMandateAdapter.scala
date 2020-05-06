package com.abaddon83.legal.adapters.ddMandateAdapters.AkkaHttp

import java.util.UUID

import com.abaddon83.legal.domainModel.ddMandates.{DDMandate, DDMandateNotAccepted}
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.BankAccountIdentity
import com.abaddon83.legal.ports.DDMandatePort
import com.abaddon83.legal.services.{ContractService, DDMandateService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DDMandateAdapter(ddMandateService: DDMandateService, contractService: ContractService)
  extends DDMandatePort{

  override def createDDMandate(bankAccountId: UUID, legalEntityCode: String): Future[DDMandateNotAccepted] = {
    Future {
      val draftDDMandate = ddMandateService.newDraftDDMandate(BankAccountIdentity(bankAccountId),legalEntityCode)
      val contact = contractService.createDDMandateContract(draftDDMandate)
      ddMandateService.createDDMandate(draftDDMandate, contact)
    }
  }

  override def findByIdDDMandate(ddMandateId: UUID): Future[DDMandate] = ???
}
