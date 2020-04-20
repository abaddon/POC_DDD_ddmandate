package com.abaddon83.legal.domainModel.ddMandates

import java.util.Date

import com.abaddon83.legal.domainModel.ddMandates.bankAccount.BankAccount

case class Debtor(
    userId: Int,
    firstName: String,
    lastName: String,
    taxCode:String,
    birthDate: Date,
    bankAccount: BankAccount) {

}







