package com.abaddon83.legal.adapters.BankAccountAdapters

import java.util.UUID

import com.abaddon83.legal.domainModel.ddMandates.Debtor
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.{BankAccount, BankAccountIdentity, EUBankAccount, UKBankAccount}
import com.abaddon83.legal.ports.BankAccountPort
import com.abaddon83.shared.DateUtils

import scala.collection.mutable.ListBuffer

class FakeBankAccountAdapter extends BankAccountPort{
  override def findDebtorByBankAccountId(bankAccountId: BankAccountIdentity): Option[Debtor] = {

    val result = bankAccountService.find(debtor =>
      debtor.bankAccount.identity == bankAccountId
    )

    result
  }

  override def findBankAccountByBankAccountId(bankAccountId: BankAccountIdentity): Option[BankAccount] = ???


  def acceptBankAccount(bankAccountId: BankAccountIdentity) = {
    val debtor = bankAccountService.find(debtor => debtor.bankAccount.identity == bankAccountId).get
    val bankAccountValid : BankAccount = debtor.bankAccount match {
      case EUBankAccount(id, iban, validated) => EUBankAccount(id,iban,true)
      case UKBankAccount(id, sortCode, accountNumber, validated) => UKBankAccount(id, sortCode, accountNumber, true)
    }

    val updatedDebtor = debtor.copy(bankAccount = bankAccountValid)
    bankAccountService-=debtor

    bankAccountService.addOne(updatedDebtor)

  }

  private val bankAccountService: ListBuffer[Debtor]= ListBuffer(
    buildDebtor("146a525d-402b-4bce-a317-3f00d05aede0",false,"IT"),
    buildDebtor("4a943d91-1ed4-4a1d-904e-9ec830106299",true,"IT"),
    buildDebtor("d4456de3-bcb0-4009-adff-803d7884c647",true,"IT"),
    buildDebtor("c9671ce9-a148-4ee0-9bae-8672fb9fbaa1",false,"IT"),
    buildDebtor("278334f4-14a2-480f-8873-ce3f840f2eb1",false,"UK"),
    buildDebtor("ace8e313-e5ed-438b-856d-9551e5cda1d1",true,"UK")
  )

  private def buildDebtor(bankAccountId: String, bankAccountValidated: Boolean, country: String): Debtor = {
    val userid = 1
    val firstName ="name"
    val lastName = "surname"
    val taxCode = "taxcode"
    val birthDate = DateUtils.buildDateFromString("2000-01-01",None)
    country match {
      case "IT" =>  Debtor(userid,firstName,lastName,taxCode,birthDate,buildEUBankAccount(bankAccountId,bankAccountValidated))
      case "UK" =>  Debtor(userid,firstName,lastName,taxCode,birthDate,buildUKBankAccount(bankAccountId,bankAccountValidated))
    }
  }

  private def buildEUBankAccount(id:String,validated:Boolean): EUBankAccount ={
    EUBankAccount(BankAccountIdentity(UUID.fromString(id)),id.substring(0,5),validated)
  }

  private def buildUKBankAccount(id:String,validated:Boolean): UKBankAccount ={
    val uuid = UUID.fromString(id)
    val sortCode = 123456
    val account = 12345678
    UKBankAccount(BankAccountIdentity(uuid),sortCode,account,validated)
  }



}


