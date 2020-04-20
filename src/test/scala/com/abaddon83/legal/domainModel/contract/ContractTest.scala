package com.abaddon83.legal.domainModel.contract

import java.util.{Date, UUID}

import com.abaddon83.legal.domainModel.contract.Repositories.{XyzRepository, Repository}
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.{BankAccount, EUBankAccount, UKBankAccount}
import com.abaddon83.legal.domainModel.ddMandates.{Creditor, DDMandate, DDMandateIdentity, DRAFT, Debtor, Financial}
import org.scalatest.funsuite.AnyFunSuite


class ContractTest extends AnyFunSuite {

  test("New dd mandate contract created") {

    val ddMandate = buildEUDDMandateDraft()

    val contract = Contract(ddMandate)

    assert(contract.contractType == DD_MANDATE)
    assert(contract.format == PDF)
    assert(contract.name == "Direct Debit Mandate"++ddMandate.identity.toString)
    assert(contract.reference == ddMandate.identity.toString)
    assert(contract.file.provider == "S3")
    assert(contract.signedFile.isEmpty)
    assert(contract.signatureDate.isEmpty)
    assert(!contract.isSigned())
  }
  test("sign DD mandate contract"){
    val ddMandate = buildEUDDMandateDraft()

    val contract = Contract(ddMandate)
    val filename = "PosteDDMandateSigned.pdf"
    val signedDate = new Date()
    val signedFile = buildSignedPosteFile(filename)
    val contractSigned = contract.sign(signedFile,signedDate)

    assert(contractSigned.signedFile.get.provider == signedFile.provider)
    assert(contractSigned.signedFile.get.url == "https://repository.xyz/"++filename)
    assert(contractSigned.isSigned())
    assert(contractSigned.file==contract.file)
    assert(contractSigned.reference==contract.reference)
    assert(contractSigned.name==contract.name)
    assert(contractSigned.format==contract.format)
    assert(contractSigned.creationDate==contract.creationDate)
    assert(contractSigned.identity==contract.identity)
    assert(contractSigned.contractType==contract.contractType)

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