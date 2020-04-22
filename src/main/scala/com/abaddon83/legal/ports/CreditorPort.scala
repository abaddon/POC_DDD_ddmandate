package com.abaddon83.legal.ports


import com.abaddon83.legal.domainModel.ddMandates.{Creditor, LegalEntityCode}

trait CreditorPort {
  def findByLegalEntity(legalEntityCode: LegalEntityCode): Option[Creditor]
}
