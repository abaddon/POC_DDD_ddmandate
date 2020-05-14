package com.abaddon83.legal.ddMandates.adapters.bankAccountAdapters.fake

import java.util.UUID

import com.abaddon83.legal.ddMandates.domainModels
import com.abaddon83.legal.ddMandates.domainModels.Debtor
import com.abaddon83.legal.ddMandates.domainModels.bankAccount.{BankAccount, EUBankAccount, UKBankAccount}
import com.abaddon83.legal.ddMandates.ports.BankAccountPort
import com.abaddon83.legal.sharedValueObjects.bankAccounts.BankAccountIdentity
import com.abaddon83.libs.DateUtils

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

class FakeBankAccountAdapter extends BankAccountPort{
  override def findDebtorByBankAccountId(bankAccountId: BankAccountIdentity): Future[Debtor] = {
    Future{
      bankAccountService.find(debtor =>
        debtor.bankAccount.identity == bankAccountId
      ).getOrElse(throw new NoSuchElementException(s"Debtor with bank account id: ${bankAccountId} not found"))
    }
  }

  override def findValidatedDebtorByBankAccountId(bankAccountId: BankAccountIdentity): Future[Debtor] = {
    findDebtorByBankAccountId(bankAccountId).filter(_.bankAccount.isValid)
  }

  override def findBankAccountByBankAccountId(bankAccountId: BankAccountIdentity): Future[BankAccount] = ???


  def acceptBankAccount(bankAccountIdentity: BankAccountIdentity) = {
    bankAccountService.find(debtor => debtor.bankAccount.identity == bankAccountIdentity) match {
      case Some(debtorUnsigned: Debtor) => update(debtorUnsigned,validate(debtorUnsigned))
    }

  }

  private def update(oldDebtor: Debtor, updatedDebtor: Debtor) = {
    bankAccountService-=oldDebtor

    bankAccountService.addOne(updatedDebtor)

  }


  private def validate(debtor: Debtor): Debtor = {
    val validatedBankAccount = debtor.bankAccount match {
      case bankAccount : EUBankAccount => bankAccount.copy(validated = true)
      case bankAccount : UKBankAccount => bankAccount.copy(validated = true)
    }
    debtor.copy(bankAccount = validatedBankAccount)
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
      case "IT" =>  domainModels.Debtor(userid,firstName,lastName,taxCode,birthDate,buildEUBankAccount(bankAccountId,bankAccountValidated))
      case "UK" =>  domainModels.Debtor(userid,firstName,lastName,taxCode,birthDate,buildUKBankAccount(bankAccountId,bankAccountValidated))
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


