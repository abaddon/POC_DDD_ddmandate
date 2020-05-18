package com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters

import java.util.UUID

import com.abaddon83.legal.ddMandates.domainModels.{DDMandate, DDMandateAccepted, DDMandateCanceled, DDMandateNotAccepted}
import com.abaddon83.legal.ddMandates.ports._
import com.abaddon83.legal.ddMandates.services.DDMandateService
import com.abaddon83.legal.sharedValueObjects.bankAccounts.BankAccountIdentity
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity

import scala.concurrent.Future

trait DDMandateControllerAdapter extends DDMandateControllerPort {

  val bankAccountPort: BankAccountPort
  val contractPort :ContractDDMandatePort
  val creditorPort : CreditorPort
  val ddMandateRepositoryPort : DDMandateRepositoryPort

  private lazy val ddMandateService: DDMandateService =   new DDMandateService(ddMandateRepositoryPort,bankAccountPort,creditorPort,contractPort)

  override def createDDMandate(bankAccountId: UUID, legalEntityCode: String): Future[DDMandateNotAccepted] = {
      val bankAccountIdentity = BankAccountIdentity(bankAccountId)
      ddMandateService.createDDMandate(bankAccountIdentity,legalEntityCode)
  }

  override def findByIdDDMandate(ddMandateId: UUID): Future[DDMandate] = {
    ddMandateService.search.findDDMandateById(DDMandateIdentity.apply(ddMandateId))

  }

  override def acceptDDMandate(ddMandateId: UUID): Future[DDMandateAccepted] = {
    val ddMandateIdentity = DDMandateIdentity.apply(ddMandateId)
    ddMandateService.acceptDDMandate(ddMandateIdentity)
  }

  override def cancelDDMandate(ddMandateId: UUID): Future[DDMandateCanceled] = {
      val ddMandateIdentity = DDMandateIdentity.apply(ddMandateId)
      ddMandateService.cancelDDMandate(ddMandateIdentity)
  }
}
