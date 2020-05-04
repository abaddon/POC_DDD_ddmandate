package com.abaddon83.legal.domainModel.ddMandates

import com.abaddon83.legal.domainModel.ddMandates.bankAccount.BankAccount

case class Creditor(
      legalEntityCode: String,
      businessName: String,
      bankAccount: BankAccount,
      debtCode: String) {
}