package com.abaddon83.legal.domainModel.ddMandates.bankAccount

import java.util.UUID

trait BankAccount{
  val identity: BankAccountIdentity
  val code: String
  var isValid: Boolean
}
