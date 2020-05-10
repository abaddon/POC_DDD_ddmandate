package com.abaddon83.legal.ports

import com.abaddon83.legal.domainModel.ddMandates.Debtor
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.{BankAccount, BankAccountIdentity}

trait BankAccountPort {
  def findDebtorByBankAccountId(bankAccountId: BankAccountIdentity): Option[Debtor]
  def findValidatedDebtorByBankAccountId(bankAccountId: BankAccountIdentity): Option[Debtor]
  def findBankAccountByBankAccountId(bankAccountId: BankAccountIdentity): Option[BankAccount]
}
