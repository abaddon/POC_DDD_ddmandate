package com.abaddon83.legal.domainModel.ddMandates.bankAccount

import java.util.UUID

case class UKBankAccount(id: UUID, sortCode: Int, accountNumber: Int, validated: Boolean) extends BankAccount{
  override val identifier: UUID = id
  override val code: String =  sortCode.toString++accountNumber.toString
  override var isValid: Boolean = validated


}