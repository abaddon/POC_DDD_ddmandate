package com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp

import java.util.UUID

import com.abaddon83.legal.ddMandates.domainModels.{DDMandate, DDMandateAccepted, DDMandateCanceled, DDMandateNotAccepted}
import com.abaddon83.legal.ddMandates.ports._
import com.abaddon83.legal.ddMandates.services.DDMandateService
import com.abaddon83.legal.sharedValueObjects.bankAccounts.BankAccountIdentity
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity
import wvlet.airframe._

import scala.concurrent.Future


trait DDMandateAdapter extends DDMandatePort{

  val bankAccountPort: BankAccountPort = bind[BankAccountPort]
  val contractPort :DDMandateContractPort = bind[DDMandateContractPort]
  val creditorPort : CreditorPort = bind[CreditorPort]
  val ddMandateRepositoryePort : DDMandateRepositoryPort = bind[DDMandateRepositoryPort]

  private lazy val ddMandateService: DDMandateService =   new DDMandateService(ddMandateRepositoryePort,bankAccountPort,creditorPort,contractPort)

  override def createDDMandate(bankAccountId: UUID, legalEntityCode: String): Future[DDMandateNotAccepted] = {
      val bankAccountIdentity = BankAccountIdentity(bankAccountId)
      ddMandateService.createDDMandate(bankAccountIdentity,legalEntityCode)
  }

  override def findByIdDDMandate(ddMandateId: UUID): Future[DDMandate] = {
    ddMandateService.search.findDDMandateById(DDMandateIdentity.apply(ddMandateId))
    /*match {
      case Some(value) => value
      case None => throw new NoSuchElementException(s"DD Mandate with id: ${ddMandateId.toString} not found ")
    }*/

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
