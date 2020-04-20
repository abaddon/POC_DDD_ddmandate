package com.abaddon83.legal.domainModel.ddMandates.bankAccount

import java.util.UUID

case class EUBankAccount(id: UUID, iban: String, validated: Boolean) extends BankAccount{
  override val code: String = iban
  override val identifier: UUID = id
  override var isValid: Boolean = validated



}
