package com.abaddon83.legal.domainModel.contract

import java.util.{Date, UUID}

import com.abaddon83.legal.domainModel.contract.Repositories.Repository
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.{BankAccount, EUBankAccount, UKBankAccount}
import com.abaddon83.legal.domainModel.ddMandates.{Creditor, DDMandateDraft, Debtor}
import org.scalatest.funsuite.AnyFunSuite


class ContractTest extends AnyFunSuite {

  test("New dd mandate contract created") {

    val ddMandate = buildDraftDDMandate(false)

    val contractUnSigned = ContractUnSigned(ddMandate,fakeRepository)

    assert(contractUnSigned.contractType == DD_MANDATE)
    assert(contractUnSigned.format == PDF)
    assert(contractUnSigned.name == "Direct Debit Mandate"++ddMandate.identity.toString)
    assert(contractUnSigned.reference == ddMandate.identity.toString)
    assert(contractUnSigned.file == fakeRepository)
  }

  test("sign a contract"){

    val unsignedContract = buildUnsignedContract()
    val signatureDate = new Date()
    val signedFile = fakeRepository
    val contractSigned = unsignedContract.sign(signedFile,signatureDate)

    assert(contractSigned.signedFile == signedFile)
    assert(contractSigned.signatureDate == signatureDate)
    assert(contractSigned.file==unsignedContract.file)
    assert(contractSigned.reference==unsignedContract.reference)
    assert(contractSigned.name==unsignedContract.name)
    assert(contractSigned.format==unsignedContract.format)
    assert(contractSigned.creationDate==unsignedContract.creationDate)
    assert(contractSigned.identity==unsignedContract.identity)
    assert(contractSigned.contractType==unsignedContract.contractType)

  }


  def buildUnsignedContract(): ContractUnSigned = {

    ContractUnSigned(buildDraftDDMandate(false),fakeRepository)
  }

  def buildDraftDDMandate(isBankAccountValid: Boolean): DDMandateDraft ={
    val debtor = buildDebtor(isBankAccountValid)
    val creditor = buildCreditor()
    DDMandateDraft(debtor,creditor)
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

  object fakeRepository extends Repository {
    override val provider: String = "Fake"

    override def url: String = "ddmandate.contract.test"
  }

}