package com.abaddon83.legal.domainModel.ddMandates
import java.util.{Date, UUID}

import com.abaddon83.legal.domainModel.contract.Contract
import com.abaddon83.legal.domainModel.contract.Repositories.{XyzRepository, Repository}
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.{BankAccount, EUBankAccount, UKBankAccount}
import org.scalatest.funsuite.AnyFunSuite

class DDMandateTest extends AnyFunSuite {
  test("create new DD Mandate"){

    val debtor = buildDebtor(false)
    val creditor = buildCreditor()

    val ddMandate = DDMandate(debtor,creditor)

    assert(ddMandate.contract.isEmpty)
    assert(ddMandate.creditor == creditor)
    assert(ddMandate.ddMandateType == Financial)
    assert(ddMandate.debtor == debtor)
    assert(ddMandate.status == DRAFT)

  }

  test("assign the right contract to a draft mandate"){
    val debtor = buildDebtor(false)
    val creditor = buildCreditor()

    val ddMandate = DDMandate(debtor,creditor)
    val contract = buildContract(ddMandate)
    val ddMandateWithContract = ddMandate.assignContract(contract)

    //assert


  }

  test("assign the wrong contract to a draft mandate"){

  }


  def buildContract(ddMandate:DDMandate):Contract={
    Contract(ddMandate)
  }

  def buildEUDDMandateDraft(): DDMandate= {

    val identity = DDMandateIdentity(UUID.fromString("25c54011-4994-4616-b9de-931083db957b"))

    val debtor = buildDebtor(false)
    val creditor = buildCreditor()
    val creationDate = buildDateFromString("2020-01-01",None)

    DDMandate(identity,Financial,debtor,creditor,creationDate,DRAFT,None)
  }

  def buildDebtor(bankAccountValidated: Boolean):Debtor = {
    val firstName = "First"
    val userId = 12345
    val lastName = "Last"
    val taxcode = "TAXCODE"
    val birthDate = buildDateFromString("2000-01-01",None)

    Debtor(userId,firstName,lastName,taxcode,birthDate,buildEUBankAccount(bankAccountValidated))
  }

  def buildEUBankAccount(validated: Boolean): BankAccount = {
    val id = UUID.fromString("b305ef5f-271b-49be-b12f-8f1ed47c4199")
    EUBankAccount(id,iban = "IT123456",validated)
  }

  def buildUKBankAccount(validated: Boolean): BankAccount = {
    val id = UUID.fromString("a462886a-2945-4dfc-bdf0-26d60fbf5868")
    UKBankAccount(id,sortCode = 123456, accountNumber = 12345678,validated)
  }

  def buildDateFromString(date: String, format: Option[String]): Date ={
    val dateFormat = new java.text.SimpleDateFormat(format.getOrElse("yyyy-MM-dd"))
    dateFormat.parse(date)
  }

  def buildCreditor(): Creditor = {
    Creditor( "Company1",buildEUBankAccount(true),"123456")
  }

  def buildSignedPosteFile(name:String) : Repository ={
    new XyzRepository(name)
  }

}
