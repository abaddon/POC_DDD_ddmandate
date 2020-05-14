package com.abaddon83.legal.ddMandates.ports

import com.abaddon83.legal.ddMandates.domainModels.Creditor

import scala.concurrent.Future

trait CreditorPort {
  def findByLegalEntity(legalEntityCode: String): Future[Creditor]
}
