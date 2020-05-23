package com.abaddon83.legal.ddMandates.adapters.ddMandateUIAdapters

import java.util.UUID

import com.abaddon83.legal.ddMandates.domainModels.{DDMandate, DDMandateAccepted, DDMandateCanceled, DDMandateNotAccepted}
import com.abaddon83.legal.ddMandates.ports._
import com.abaddon83.legal.ddMandates.services.DDMandateService
import com.abaddon83.legal.shares.bankAccounts.BankAccountIdentity
import com.abaddon83.legal.shares.ddMandates.DDMandateIdentity

import scala.concurrent.Future

trait DDMandateUIAdapter extends DDMandateUIPort {

  val bankAccountPort: BankAccountPort
  val contractPort :DDMandateContractPort
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
