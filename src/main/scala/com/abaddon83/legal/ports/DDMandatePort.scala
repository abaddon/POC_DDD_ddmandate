package com.abaddon83.legal.ports

import java.util.UUID

import com.abaddon83.legal.domainModel.ddMandates.DDMandateNotAccepted

import scala.concurrent.Future

trait DDMandatePort {
  def createDDMandate(bankAccountId: UUID, legalEntityCode: String): Future[DDMandateNotAccepted]
}
