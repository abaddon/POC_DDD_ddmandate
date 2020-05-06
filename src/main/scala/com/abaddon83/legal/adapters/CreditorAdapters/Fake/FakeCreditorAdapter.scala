package com.abaddon83.legal.adapters.CreditorAdapters.Fake

import java.util.UUID

import com.abaddon83.legal.domainModel.ddMandates.Creditor
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.{BankAccountIdentity, EUBankAccount, UKBankAccount}
import com.abaddon83.legal.ports.CreditorPort

class FakeCreditorAdapter extends CreditorPort{
  override def findByLegalEntity(legalEntityCode: String): Option[Creditor] = {
    creditorList.find(creditor => creditor.legalEntityCode == legalEntityCode)
  }

  private val creditorList : List[Creditor] = List(
    Creditor("UK1","UK company",buildUKBankAccount("a924b50d-2e31-49f8-b83b-553799093644",true),"debtCode-1234"),
    Creditor("IT1","IT company",buildEUBankAccount("58d23930-999f-413d-abf8-c2a98f28af4f",true),"debtCode-1235"),
    Creditor("DE1","DE company",buildEUBankAccount("8c3f4c40-e31d-4110-a151-fa6bd37653f1",true),"debtCode-1236")
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
