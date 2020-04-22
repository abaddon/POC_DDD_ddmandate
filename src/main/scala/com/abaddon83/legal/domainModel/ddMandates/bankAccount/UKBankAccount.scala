package com.abaddon83.legal.domainModel.ddMandates.bankAccount

import java.util.UUID

case class UKBankAccount(id: BankAccountIdentity, sortCode: Int, accountNumber: Int, validated: Boolean) extends BankAccount{
  override val identity: BankAccountIdentity = id
  override val code: String =  sortCode.toString++accountNumber.toString
  override var isValid: Boolean = validated
}