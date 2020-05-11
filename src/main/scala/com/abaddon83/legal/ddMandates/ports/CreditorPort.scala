package com.abaddon83.legal.ddMandates.ports

import com.abaddon83.legal.ddMandates.domainModels.Creditor

trait CreditorPort {
  def findByLegalEntity(legalEntityCode: String): Option[Creditor]
}
