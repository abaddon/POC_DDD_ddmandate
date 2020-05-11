package com.abaddon83.legal.ddMandates.ports

import java.util.UUID

import com.abaddon83.legal.ddMandates.domainModels.{DDMandate, DDMandateAccepted, DDMandateNotAccepted}


import scala.concurrent.Future

trait DDMandatePort {
  def createDDMandate(bankAccountId: UUID, legalEntityCode: String): Future[DDMandateNotAccepted]
  def findByIdDDMandate(ddMandateId: UUID): Future[DDMandate]
  def acceptDDMandate(ddMandateId: UUID): Future[DDMandateAccepted]
}
