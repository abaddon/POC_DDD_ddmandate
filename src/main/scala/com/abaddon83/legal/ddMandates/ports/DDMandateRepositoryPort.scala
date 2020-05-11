package com.abaddon83.legal.ddMandates.ports

import com.abaddon83.legal.ddMandates.domainModels.{DDMandate, DDMandateAccepted, DDMandateCanceled, DDMandateNotAccepted}

import com.abaddon83.legal.sharedValueObjects.bankAccounts.BankAccountIdentity
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity

trait DDMandateRepositoryPort {
  def findDDMandateNotAcceptedById(id: DDMandateIdentity): Option[DDMandateNotAccepted]
  def findDDMandateAcceptedById(id: DDMandateIdentity): Option[DDMandateAccepted]
  def findDDMandateCancelledById(id: DDMandateIdentity): Option[DDMandateCanceled]
  def findDDMandateById(id: DDMandateIdentity): Option[DDMandate]
  def findAllDDMandatesByBankAccount(id: BankAccountIdentity): List[DDMandate]



  def save(DDMandate: DDMandateNotAccepted): DDMandateNotAccepted
  def save(DDMandate: DDMandateAccepted): DDMandateAccepted
  def save(DDMandate: DDMandateCanceled): DDMandateCanceled
}
