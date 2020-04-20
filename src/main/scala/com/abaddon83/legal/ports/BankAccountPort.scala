package com.abaddon83.legal.ports

import java.util.UUID

import com.abaddon83.legal.domainModel.ddMandates.Debtor
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.BankAccount

trait BankAccountPort {
  def findDebtorByBankAccountId(bankAccountId: UUID): Debtor
  def findBankAccountByBankAccountId(bankAccountId: UUID): BankAccount
}
