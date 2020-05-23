package com.abaddon83.legal.ddMandates.adapters.CreditorAdapters.fake

import java.util.UUID

import com.abaddon83.legal.ddMandates.domainModels
import com.abaddon83.legal.ddMandates.domainModels.Creditor
import com.abaddon83.legal.ddMandates.domainModels.bankAccount.{EUBankAccount, UKBankAccount}
import com.abaddon83.legal.ddMandates.ports.CreditorPort
import com.abaddon83.legal.shares.bankAccounts.BankAccountIdentity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FakeCreditorAdapter extends CreditorPort{
  override def findByLegalEntity(legalEntityCode: String): Future[Creditor] = {
    Future{
      creditorList.find(creditor => creditor.legalEntityCode == legalEntityCode).getOrElse(throw new NoSuchElementException(s"Creditor with legalEntity: ${legalEntityCode} not found"))
    }
  }

  private val creditorList : List[Creditor] = List(
    domainModels.Creditor("UK1","UK company",buildUKBankAccount("a924b50d-2e31-49f8-b83b-553799093644",true),"debtCode-1234"),
    domainModels.Creditor("IT1","IT company",buildEUBankAccount("58d23930-999f-413d-abf8-c2a98f28af4f",true),"debtCode-1235"),
    domainModels.Creditor("DE1","DE company",buildEUBankAccount("8c3f4c40-e31d-4110-a151-fa6bd37653f1",true),"debtCode-1236")
  )

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
