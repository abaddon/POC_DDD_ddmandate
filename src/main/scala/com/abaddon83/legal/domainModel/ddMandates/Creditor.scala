package com.abaddon83.legal.domainModel.ddMandates

import com.abaddon83.legal.domainModel.ddMandates.bankAccount.BankAccount

case class Creditor(
      legalEntityCode: LegalEntityCode,
      businessName: String,
      bankAccount: BankAccount,
      debtCode: String) {
}

sealed trait LegalEntityCode
case object UK1 extends LegalEntityCode
case object IT1 extends LegalEntityCode
case object DE1 extends LegalEntityCode
