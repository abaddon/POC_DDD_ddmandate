package com.abaddon83.legal.domainModel.ddMandates
import java.util.Date

import com.abaddon83.legal.domainModel.contract.{ContractSigned, ContractUnSigned, DD_MANDATE}
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.BankAccountIdentity
import com.abaddon83.legal.tests.utilities.DomainElementHelper
import org.scalatest.funsuite.AnyFunSuite

class DDMandateTest extends AnyFunSuite with DomainElementHelper{
  test("create new DD Mandate"){

    val debtor = buildDebtor(buildEUBankAccount(false))
    val creditor = buildCreditor()

    val ddMandate = DDMandateDraft(debtor,creditor)

    assert(ddMandate.creditor == creditor)
    assert(ddMandate.ddMandateType == Financial)
    assert(ddMandate.debtor == debtor)
    assert(ddMandate.isInstanceOf[DDMandateDraft])

  }

  test("assign the right contract to a draft mandate"){

    val ddMandateDraft = buildDraftDDMandate(buildEUBankAccount(false))
    val contract = buildContract(ddMandateDraft,false)
    val ddMandateNotAccepted = ddMandateDraft.assignContract(contract)

    assert(ddMandateNotAccepted.isInstanceOf[DDMandateNotAccepted])
    assert(ddMandateNotAccepted.identity == ddMandateDraft.identity)
    assert(ddMandateNotAccepted.creationDate == ddMandateDraft.creationDate)
    assert(ddMandateNotAccepted.creditor == ddMandateDraft.creditor)
    assert(ddMandateNotAccepted.debtor == ddMandateDraft.debtor)
    assert(ddMandateNotAccepted.ddMandateType == ddMandateDraft.ddMandateType)

    assert(ddMandateNotAccepted.contract == contract)
    assert(ddMandateNotAccepted.contract.reference == ddMandateDraft.identity.uuid.toString)
    assert(ddMandateNotAccepted.contract.contractType == DD_MANDATE)

  }

  test("assign the wrong contract to a draft mandate"){

    val ddMandateDraft = buildDraftDDMandate(buildEUBankAccount(false))
    val differentDDMandateDraft = buildDraftDDMandate(buildEUBankAccount(false))
    val wrongContract = buildContract(differentDDMandateDraft,false)

    assertThrows[IllegalArgumentException] {
      ddMandateDraft.assignContract(wrongContract)
    }
  }

  test("update a wrong debtor to a not accepted mandate"){
    val bankAccountId : BankAccountIdentity = BankAccountIdentity()
    val ddMandateNotAccepted = buildNotAcceptedDDMandate(buildEUBankAccount(false,bankAccountId),isContractSigned = false)
    val validatedDebtor = buildDebtor(buildEUBankAccount(true))

    assertThrows[java.lang.AssertionError] {
      ddMandateNotAccepted.updateDebtorValidated(validatedDebtor)
    }
  }

  test("update the debtor with bank account valid to a not accepted mandate"){
    val bankAccountId : BankAccountIdentity = BankAccountIdentity()
    val ddMandateNotAccepted = buildNotAcceptedDDMandate(buildEUBankAccount(false,bankAccountId),isContractSigned = false)
    assert(!ddMandateNotAccepted.debtor.bankAccount.isValid)

    val bankAccountValid  = buildEUBankAccount(true,bankAccountId)
    val validatedDebtor = ddMandateNotAccepted.debtor.copy(bankAccount = bankAccountValid)
    val ddMandateNotAcceptedUpdated = ddMandateNotAccepted.updateDebtorValidated(validatedDebtor)

    assert(ddMandateNotAccepted.isInstanceOf[DDMandateNotAccepted])
    assert(ddMandateNotAcceptedUpdated.identity == ddMandateNotAccepted.identity)
    assert(ddMandateNotAcceptedUpdated.creationDate == ddMandateNotAccepted.creationDate)
    assert(ddMandateNotAcceptedUpdated.creditor == ddMandateNotAccepted.creditor)
    assert(ddMandateNotAcceptedUpdated.ddMandateType == ddMandateNotAccepted.ddMandateType)
    assert(ddMandateNotAcceptedUpdated.contract == ddMandateNotAccepted.contract)
    assert(ddMandateNotAcceptedUpdated.debtor.birthDate == ddMandateNotAccepted.debtor.birthDate)
    assert(ddMandateNotAcceptedUpdated.debtor.firstName == ddMandateNotAccepted.debtor.firstName)
    assert(ddMandateNotAcceptedUpdated.debtor.lastName == ddMandateNotAccepted.debtor.lastName)
    assert(ddMandateNotAcceptedUpdated.debtor.taxCode == ddMandateNotAccepted.debtor.taxCode)
    assert(ddMandateNotAcceptedUpdated.debtor.bankAccount.identity == ddMandateNotAccepted.debtor.bankAccount.identity)
    assert(ddMandateNotAcceptedUpdated.debtor.bankAccount.isValid)

  }

  test("update a wrong signedContract to a not accepted mandate"){
    val bankAccountId : BankAccountIdentity = BankAccountIdentity()
    val ddMandateNotAccepted = buildNotAcceptedDDMandate(buildEUBankAccount(false,bankAccountId),isContractSigned = false)

    assertThrows[java.lang.AssertionError] {
      ddMandateNotAccepted.updateContractSigned(buildSignedContract)
    }
  }

  test("update the correct signedContract to a not accepted mandate"){
    val bankAccountId : BankAccountIdentity = BankAccountIdentity()
    val ddMandateNotAccepted = buildNotAcceptedDDMandate(buildEUBankAccount(false,bankAccountId),isContractSigned = false)
    val currentUnSignedContract=  ddMandateNotAccepted.contract
    val signedContract = ContractSigned(currentUnSignedContract.identity,currentUnSignedContract.contractType,currentUnSignedContract.reference,currentUnSignedContract.name,currentUnSignedContract.format,currentUnSignedContract.file,currentUnSignedContract.creationDate,fakeFileRepository,new Date())


    val ddMandateNotAcceptedSigned = ddMandateNotAccepted.updateContractSigned(signedContract)

    assert(ddMandateNotAcceptedSigned.contract.isInstanceOf[ContractSigned])
    assert(ddMandateNotAcceptedSigned.contract.identity == ddMandateNotAccepted.contract.identity)
  }

  test("accept DD mandate with a contract not signed"){

    val ddMandateNotAccepted = buildNotAcceptedDDMandate(buildEUBankAccount(true),isContractSigned = false)

    assertThrows[java.lang.AssertionError] {
      ddMandateNotAccepted.accept()
    }

  }

  test("accept DD mandate with a bank account not valid"){

    val ddMandateNotAccepted = buildNotAcceptedDDMandate(buildEUBankAccount(false),isContractSigned = true)

    assertThrows[java.lang.AssertionError] {
      ddMandateNotAccepted.accept()
    }

  }

  test("accept DD mandate with a bank account not valid and contract not signed"){

    val ddMandateNotAccepted = buildNotAcceptedDDMandate(buildEUBankAccount(false),isContractSigned = false)

    assertThrows[java.lang.AssertionError] {
      ddMandateNotAccepted.accept()
    }

  }

  test("accept DD mandate with a bank account valid and contract signed"){

    val ddMandateNotAccepted = buildNotAcceptedDDMandate(buildEUBankAccount(true),isContractSigned = true)
    val ddMandateAccepted = ddMandateNotAccepted.accept()

    assert(ddMandateAccepted.isInstanceOf[DDMandateAccepted])
    assert(ddMandateAccepted.identity == ddMandateNotAccepted.identity)
    assert(ddMandateAccepted.creationDate == ddMandateNotAccepted.creationDate)
    assert(ddMandateAccepted.creditor == ddMandateNotAccepted.creditor)
    assert(ddMandateAccepted.debtor == ddMandateNotAccepted.debtor)
    assert(ddMandateAccepted.ddMandateType == ddMandateNotAccepted.ddMandateType)
    assert(ddMandateAccepted.contract == ddMandateNotAccepted.contract)


  }

  test("cancel a DD mandateAccepted"){

    val ddMandateAccepted = buildAcceptedDDMandate
    val ddMandateCanceled = ddMandateAccepted.cancel()

    assert(ddMandateCanceled.isInstanceOf[DDMandateCanceled])
    assert(ddMandateCanceled.identity == ddMandateAccepted.identity)
    assert(ddMandateCanceled.creationDate == ddMandateAccepted.creationDate)
    assert(ddMandateCanceled.creditor == ddMandateAccepted.creditor)
    assert(ddMandateCanceled.debtor == ddMandateAccepted.debtor)
    assert(ddMandateCanceled.ddMandateType == ddMandateAccepted.ddMandateType)
    assert(ddMandateCanceled.contract == ddMandateAccepted.contract)

  }






  private def buildDateFromString(date: String, format: Option[String]): Date ={
    val dateFormat = new java.text.SimpleDateFormat(format.getOrElse("yyyy-MM-dd"))
    dateFormat.parse(date)
  }





}
