package com.abaddon83.legal.ddMandates.domainModels

import com.abaddon83.legal.ddMandates.domainModels.bankAccount.BankAccount

case class Creditor(
      legalEntityCode: String,
      businessName: String,
      bankAccount: BankAccount,
      debtCode: String) {
}