package com.abaddon83.legal.ddMandates.domainModels

import java.util.Date

import com.abaddon83.legal.ddMandates.domainModels.bankAccount.BankAccount

case class Debtor(
    userId: Int,
    firstName: String,
    lastName: String,
    taxCode:String,
    birthDate: Date,
    bankAccount: BankAccount) {

}







