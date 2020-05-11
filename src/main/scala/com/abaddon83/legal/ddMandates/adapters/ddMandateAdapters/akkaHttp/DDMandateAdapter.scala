package com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp

import java.util.UUID

import com.abaddon83.legal.ddMandates.domainModels.{DDMandate, DDMandateAccepted, DDMandateCanceled, DDMandateNotAccepted}
import com.abaddon83.legal.ddMandates.ports.DDMandatePort
import com.abaddon83.legal.ddMandates.services.DDMandateService
import com.abaddon83.legal.sharedValueObjects.bankAccounts.BankAccountIdentity
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DDMandateAdapter(ddMandateService: DDMandateService)
  extends DDMandatePort{

  override def createDDMandate(bankAccountId: UUID, legalEntityCode: String): Future[DDMandateNotAccepted] = {
    Future {
      val bankAccountIdentity = BankAccountIdentity(bankAccountId)
      ddMandateService.createDDMandate(bankAccountIdentity,legalEntityCode)
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

  override def acceptDDMandate(ddMandateId: UUID): Future[DDMandateAccepted] = {
    Future {
      val ddMandateIdentity = DDMandateIdentity.apply(ddMandateId)
      ddMandateService.acceptDDMandate(ddMandateIdentity)
    }
  }

  override def cancelDDMandate(ddMandateId: UUID): Future[DDMandateCanceled] = {
    Future {
      val ddMandateIdentity = DDMandateIdentity.apply(ddMandateId)
      ddMandateService.cancelDDMandate(ddMandateIdentity)
    }
  }
}
