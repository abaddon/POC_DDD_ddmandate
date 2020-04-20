package com.abaddon83.legal.ports


import com.abaddon83.legal.domainModel.ddMandates.Creditor

trait CreditorPort {
  def findByLegalEntity(legalEntity: String): Creditor
}
