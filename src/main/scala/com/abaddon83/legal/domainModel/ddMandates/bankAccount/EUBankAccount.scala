package com.abaddon83.legal.domainModel.ddMandates.bankAccount

import java.util.UUID

case class EUBankAccount(id: BankAccountIdentity, iban: String, validated: Boolean) extends BankAccount{
  override val code: String = iban
  override val identity: BankAccountIdentity = id
  override var isValid: Boolean = validated



}
