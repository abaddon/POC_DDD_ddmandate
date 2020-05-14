package com.abaddon83.legal.ddMandates.ports

import com.abaddon83.legal.ddMandates.domainModels.Debtor
import com.abaddon83.legal.ddMandates.domainModels.bankAccount.BankAccount
import com.abaddon83.legal.sharedValueObjects.bankAccounts.BankAccountIdentity

import scala.concurrent.Future

trait BankAccountPort {
  def findDebtorByBankAccountId(bankAccountId: BankAccountIdentity): Future[Debtor]
  def findValidatedDebtorByBankAccountId(bankAccountId: BankAccountIdentity): Future[Debtor]
  def findBankAccountByBankAccountId(bankAccountId: BankAccountIdentity): Future[BankAccount]
}
