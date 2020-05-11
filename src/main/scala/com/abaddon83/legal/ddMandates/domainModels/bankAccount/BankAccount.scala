package com.abaddon83.legal.ddMandates.domainModels.bankAccount

import com.abaddon83.legal.sharedValueObjects.bankAccounts.BankAccountIdentity

trait BankAccount{
  val identity: BankAccountIdentity
  val code: String
  var isValid: Boolean
}
