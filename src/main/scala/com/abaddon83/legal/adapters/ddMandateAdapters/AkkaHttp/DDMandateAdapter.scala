package com.abaddon83.legal.adapters.ddMandateAdapters.AkkaHttp

import java.util.UUID

import com.abaddon83.legal.domainModel.ddMandates.{DDMandate, DDMandateAccepted, DDMandateIdentity, DDMandateNotAccepted}
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

  override def findByIdDDMandate(ddMandateId: UUID): Future[DDMandate] = {
    Future {
      ddMandateService.search.findDDMandateById(DDMandateIdentity.apply(ddMandateId)) match {
        case Some(value) => value
        case None => throw new NoSuchElementException(s"DD Mandate with id: ${ddMandateId.toString} not found ")
      }
    }
  }

  override def acceptDDMandate(ddMandateId: UUID, commandUUID: UUID): Future[DDMandateAccepted] = {
    Future {
      val ddMandateIdentity = DDMandateIdentity.apply(ddMandateId)
      val ddMandateNotAccepted = ddMandateService.search.findDDMandateNotAcceptedById(ddMandateIdentity) match {
        case Some(value) => value
        case None => throw new NoSuchElementException(s"DD Mandate with id: ${ddMandateId.toString} not found ")
      }

      val contractIdentity = ddMandateNotAccepted.contract.identity

      val contractSigned = contractService.search().findByContractSignedByIdentity(contractIdentity) match {
        case Some(value) => value
        case None => throw new NoSuchElementException(s"Signed Contract with id: ${contractIdentity.toString} not found ")
      }

      ddMandateService.acceptDDMandate(ddMandateIdentity,contractSigned)


    }
  }
}
