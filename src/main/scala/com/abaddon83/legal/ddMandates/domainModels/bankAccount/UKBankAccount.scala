package com.abaddon83.legal.ddMandates.domainModels.bankAccount

import java.util.UUID

import com.abaddon83.legal.sharedValueObjects.bankAccounts.BankAccountIdentity

case class UKBankAccount(id: BankAccountIdentity, sortCode: Int, accountNumber: Int, validated: Boolean) extends BankAccount{
  override val identity: BankAccountIdentity = id
  override val code: String =  sortCode.toString++accountNumber.toString
  override var isValid: Boolean = validated
}