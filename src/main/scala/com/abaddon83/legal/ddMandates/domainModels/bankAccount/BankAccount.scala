package com.abaddon83.legal.ddMandates.domainModels.bankAccount

import com.abaddon83.legal.shares.bankAccounts.BankAccountIdentity

trait BankAccount{
  val identity: BankAccountIdentity
  val code: String
  var isValid: Boolean
}
