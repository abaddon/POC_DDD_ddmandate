package com.abaddon83.legal.ddMandates.ports

import com.abaddon83.legal.ddMandates.domainModels.{DDMandate, DDMandateAccepted, DDMandateCanceled, DDMandateNotAccepted}
import com.abaddon83.legal.shares.bankAccounts.BankAccountIdentity
import com.abaddon83.legal.shares.ddMandates.DDMandateIdentity

import scala.concurrent.Future

trait DDMandateRepositoryPort {
  def findDDMandateNotAcceptedById(id: DDMandateIdentity): Future[DDMandateNotAccepted]
  def findDDMandateAcceptedById(id: DDMandateIdentity): Future[DDMandateAccepted]
  def findDDMandateCancelledById(id: DDMandateIdentity): Future[DDMandateCanceled]
  def findDDMandateById(id: DDMandateIdentity): Future[DDMandate]
  def findAllDDMandatesByBankAccount(id: BankAccountIdentity): Future[List[DDMandate]]



  def save(DDMandate: DDMandateNotAccepted): DDMandateNotAccepted
  def save(DDMandate: DDMandateAccepted): DDMandateAccepted
  def save(DDMandate: DDMandateCanceled): DDMandateCanceled
}
