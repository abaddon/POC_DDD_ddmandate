package com.abaddon83.legal.ports

import com.abaddon83.legal.domainModel.ddMandates.bankAccount.BankAccountIdentity
import com.abaddon83.legal.domainModel.ddMandates.{DDMandate, DDMandateAccepted, DDMandateCanceled, DDMandateIdentity, DDMandateNotAccepted}

trait DDMandateRepositoryPort {
  def findDDMandateNotAcceptedById(id: DDMandateIdentity): Option[DDMandateNotAccepted]
  def findDDMandateAcceptedById(id: DDMandateIdentity): Option[DDMandateAccepted]
  def findDDMandateById(id: DDMandateIdentity): Option[DDMandate]
  def findAllDDMandatesByBankAccount(id: BankAccountIdentity): List[DDMandate]



  def save(DDMandate: DDMandateNotAccepted): DDMandateNotAccepted
  def save(DDMandate: DDMandateAccepted): DDMandateAccepted
  def save(DDMandate: DDMandateCanceled): DDMandateCanceled
}
