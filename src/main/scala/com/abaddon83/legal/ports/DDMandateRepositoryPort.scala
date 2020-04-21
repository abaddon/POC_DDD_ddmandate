package com.abaddon83.legal.ports

import com.abaddon83.legal.domainModel.ddMandates.{DDMandate, DDMandateAccepted, DDMandateCanceled, DDMandateIdentity, DDMandateNotAccepted}

trait DDMandateRepositoryPort {
  def findDDMandateById(id: DDMandateIdentity): DDMandate
  def findDDMandateNotAcceptedById(id: DDMandateIdentity): DDMandateNotAccepted
  def findDDMandateAcceptedById(id: DDMandateIdentity): DDMandateAccepted

  def save(DDMandate: DDMandateNotAccepted): DDMandateNotAccepted
  def save(DDMandate: DDMandateAccepted): DDMandateAccepted
  def save(DDMandate: DDMandateCanceled): DDMandateCanceled
}
