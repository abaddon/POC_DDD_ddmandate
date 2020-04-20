package com.abaddon83.legal.ports

import com.abaddon83.legal.domainModel.ddMandates.{DDMandate, DDMandateIdentity}

trait DDMandateRepositoryPort {
  def findDDMandateById(id: DDMandateIdentity): DDMandate
  def save(DDMandate: DDMandate): DDMandate
}
