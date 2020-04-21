package com.abaddon83.legal.domainModel.ddMandates
import java.util.{Date, UUID}

import com.abaddon83.legal.domainModel.contract.{Contract, ContractSigned, ContractUnSigned, DD_MANDATE}
import com.abaddon83.legal.domainModel.contract.Repositories.Repository
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.{BankAccount, EUBankAccount, UKBankAccount}
import org.scalatest.funsuite.AnyFunSuite

class DDMandateTest extends AnyFunSuite {
  test("create new DD Mandate"){

    val debtor = buildDebtor(false)
    val creditor = buildCreditor()

    val ddMandate = DDMandateDraft(debtor,creditor)

    assert(ddMandate.creditor == creditor)
    assert(ddMandate.ddMandateType == Financial)
    assert(ddMandate.debtor == debtor)
    assert(ddMandate.status == DRAFT)

  }

  test("assign the right contract to a draft mandate"){

    val ddMandateDraft = buildDraftDDMandate(false)
    val contract = buildContract(ddMandateDraft,false)
    val ddMandateNotAccepted = ddMandateDraft.assignContract(contract)

    assert(ddMandateNotAccepted.identity == ddMandateDraft.identity)
    assert(ddMandateNotAccepted.creationDate == ddMandateDraft.creationDate)
    assert(ddMandateNotAccepted.creditor == ddMandateDraft.creditor)
    assert(ddMandateNotAccepted.debtor == ddMandateDraft.debtor)
    assert(ddMandateNotAccepted.ddMandateType == ddMandateDraft.ddMandateType)
    assert(ddMandateNotAccepted.status == NOACCEPTED)

    assert(ddMandateNotAccepted.contract == contract)
    assert(ddMandateNotAccepted.contract.reference == ddMandateDraft.identity.toString)
    assert(ddMandateNotAccepted.contract.contractType == DD_MANDATE)


  }

  test("assign the wrong contract to a draft mandate"){

    val ddMandateDraft = buildDraftDDMandate(isBankAccountValid = false)
    val differentDDMandateDraft = buildDraftDDMandate(isBankAccountValid = false)
    val wrongContract = buildContract(differentDDMandateDraft,false)

    assertThrows[java.lang.AssertionError] {
      ddMandateDraft.assignContract(wrongContract)
    }
  }

  test("accept DD mandate with a contract not signed"){

    val ddMandateNotAccepted = buildNotAcceptedDDMandate(isBankAccountValid = true,isContractSigned = false)

    assertThrows[java.lang.AssertionError] {
      ddMandateNotAccepted.accept()
    }

  }

  test("accept DD mandate with a bank account not valid"){

    val ddMandateNotAccepted = buildNotAcceptedDDMandate(isBankAccountValid = false,isContractSigned = true)

    assertThrows[java.lang.AssertionError] {
      ddMandateNotAccepted.accept()
    }

  }

  test("accept DD mandate with a bank account not valid and contract not signed"){

    val ddMandateNotAccepted = buildNotAcceptedDDMandate(isBankAccountValid = false,isContractSigned = false)

    assertThrows[java.lang.AssertionError] {
      ddMandateNotAccepted.accept()
    }

  }

  test("accept DD mandate with a bank account valid and contract signed"){

    val ddMandateNotAccepted = buildNotAcceptedDDMandate(isBankAccountValid = true,isContractSigned = true)
    val ddMandateAccepted = ddMandateNotAccepted.accept()

    assert(ddMandateAccepted.identity == ddMandateNotAccepted.identity)
    assert(ddMandateAccepted.creationDate == ddMandateNotAccepted.creationDate)
    assert(ddMandateAccepted.creditor == ddMandateNotAccepted.creditor)
    assert(ddMandateAccepted.debtor == ddMandateNotAccepted.debtor)
    assert(ddMandateAccepted.ddMandateType == ddMandateNotAccepted.ddMandateType)
    assert(ddMandateAccepted.contract == ddMandateNotAccepted.contract)
    assert(ddMandateAccepted.status == ACCEPTED)

  }

  test("cancel a DD mandateAccepted"){

    val ddMandateAccepted = buildAcceptedDDMandate
    val ddMandateCanceled = ddMandateAccepted.cancel()

    assert(ddMandateCanceled.identity == ddMandateAccepted.identity)
    assert(ddMandateCanceled.creationDate == ddMandateAccepted.creationDate)
    assert(ddMandateCanceled.creditor == ddMandateAccepted.creditor)
    assert(ddMandateCanceled.debtor == ddMandateAccepted.debtor)
    assert(ddMandateCanceled.ddMandateType == ddMandateAccepted.ddMandateType)
    assert(ddMandateCanceled.contract == ddMandateAccepted.contract)
    assert(ddMandateCanceled.status == CANCELED)

  }


  def buildDraftDDMandate(isBankAccountValid: Boolean): DDMandateDraft ={
    val debtor = buildDebtor(isBankAccountValid)
    val creditor = buildCreditor()
    DDMandateDraft(debtor,creditor)
  }

  def buildNotAcceptedDDMandate(isBankAccountValid: Boolean,isContractSigned: Boolean): DDMandateNotAccepted = {
    val ddMandateDraft = buildDraftDDMandate(isBankAccountValid)
    val contract = buildContract(ddMandateDraft,isContractSigned)
    ddMandateDraft.assignContract(contract)
  }

  def buildAcceptedDDMandate : DDMandateAccepted = {
    val ddMandateNotAccepted = buildNotAcceptedDDMandate(isBankAccountValid = true,isContractSigned = true)
    ddMandateNotAccepted.accept()
  }

  def buildContract(ddMandate:DDMandate, isSigned: Boolean):Contract={
    isSigned match {
      case true => ContractUnSigned(ddMandate,fakeRepository).sign(fakeRepository,new Date)
      case false => ContractUnSigned(ddMandate,fakeRepository)
    }
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
