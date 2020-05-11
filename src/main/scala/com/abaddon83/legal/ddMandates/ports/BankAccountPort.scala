package com.abaddon83.legal.ddMandates.ports

import com.abaddon83.legal.ddMandates.domainModels.Debtor
import com.abaddon83.legal.ddMandates.domainModels.bankAccount.BankAccount
import com.abaddon83.legal.sharedValueObjects.bankAccounts.BankAccountIdentity

trait BankAccountPort {
  def findDebtorByBankAccountId(bankAccountId: BankAccountIdentity): Option[Debtor]
  def findValidatedDebtorByBankAccountId(bankAccountId: BankAccountIdentity): Option[Debtor]
  def findBankAccountByBankAccountId(bankAccountId: BankAccountIdentity): Option[BankAccount]
}
