package com.abaddon83.legal.ddMandates.domainModels

import java.util.Date

import com.abaddon83.legal.shares.bankAccounts.BankAccountIdentity
import com.abaddon83.legal.shares.contracts.DD_MANDATE
import com.abaddon83.legal.utilities.DDMandateDomainElementHelper
import org.scalatest.funsuite.AnyFunSuite

class DDMandateTest extends AnyFunSuite with DDMandateDomainElementHelper{
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
    assert(ddMandateNotAccepted.contract.reference == ddMandateDraft.identity.convertTo().toString)
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



  test("update a wrong signedContract to a not accepted mandate"){
    val bankAccountId : BankAccountIdentity = BankAccountIdentity.randomIdentity()
    val ddMandateNotAccepted = buildNotAcceptedDDMandate(buildEUBankAccount(false,bankAccountId),isContractSigned = false)

    assertThrows[java.lang.AssertionError] {
      ddMandateNotAccepted.updateContractSigned(buildSignedContract)
    }
  }

  test("update the correct signedContract to a not accepted mandate"){
    val bankAccountId : BankAccountIdentity = BankAccountIdentity.randomIdentity()
    val ddMandateNotAccepted = buildNotAcceptedDDMandate(buildEUBankAccount(false,bankAccountId),isContractSigned = false)
    val currentUnSignedContract=  ddMandateNotAccepted.contract
    val signedContract = DDMandateContract(currentUnSignedContract.identity,currentUnSignedContract.reference,currentUnSignedContract.contractType,currentUnSignedContract.name,currentUnSignedContract.format,currentUnSignedContract.creationDate,Some(new Date()))


    val ddMandateNotAcceptedSigned = ddMandateNotAccepted.updateContractSigned(signedContract)

    assert(ddMandateNotAcceptedSigned.contract.isSigned)
    assert(ddMandateNotAcceptedSigned.contract.identity == ddMandateNotAccepted.contract.identity)
  }


  test("accept DD mandate with a bank account not valid"){

    val bankAccount = buildEUBankAccount(false)
    val ddMandateNotAccepted = buildNotAcceptedDDMandate(bankAccount,isContractSigned = true)
    val contractSigned = ddMandateNotAccepted.contract
    val debtor = ddMandateNotAccepted.debtor

    assertThrows[java.lang.AssertionError] {
      ddMandateNotAccepted.accept(contractSigned,debtor)
    }

  }

  test("accept DD mandate with a bank account valid and contract signed"){

    val ddMandateNotAccepted = buildNotAcceptedDDMandate(buildEUBankAccount(true),isContractSigned = true)
    val contractSigned = ddMandateNotAccepted.contract
    val debtor = ddMandateNotAccepted.debtor

    val ddMandateAccepted = ddMandateNotAccepted.accept(contractSigned,debtor)

    assert(ddMandateAccepted.isInstanceOf[DDMandateAccepted])
    assert(ddMandateAccepted.identity == ddMandateNotAccepted.identity)
    assert(ddMandateAccepted.creationDate == ddMandateNotAccepted.creationDate)
    assert(ddMandateAccepted.creditor == ddMandateNotAccepted.creditor)
    assert(ddMandateAccepted.debtor == ddMandateNotAccepted.debtor)
    assert(ddMandateAccepted.ddMandateType == ddMandateNotAccepted.ddMandateType)
    assert(ddMandateAccepted.contract == ddMandateNotAccepted.contract)
    assert(ddMandateAccepted.contract.isSigned )


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
