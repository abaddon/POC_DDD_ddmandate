package com.abaddon83.legal.ddMandates.domainModels.bankAccount

import com.abaddon83.legal.sharedValueObjects.bankAccounts.BankAccountIdentity

case class EUBankAccount(id: BankAccountIdentity, iban: String, validated: Boolean) extends BankAccount{
  override val code: String = iban
  override val identity: BankAccountIdentity = id
  override var isValid: Boolean = validated



}
