package com.abaddon83.legal.domainModel.contract

import java.util.{Date, UUID}

import com.abaddon83.legal.domainModel.contract.FileRepositories.FileRepository
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.{BankAccount, BankAccountIdentity, EUBankAccount, UKBankAccount}
import com.abaddon83.legal.domainModel.ddMandates.{Creditor, DDMandateDraft, Debtor, IT1}
import com.abaddon83.legal.tests.utilities.DomainElementHelper
import org.scalatest.funsuite.AnyFunSuite


class ContractTest extends AnyFunSuite with DomainElementHelper{

  test("New dd mandate contract created") {

    val ddMandate = buildDraftDDMandate(buildEUBankAccount(false))

    val contractUnSigned = ContractUnSigned(ddMandate,Some(fakeFileRepository))

    assert(contractUnSigned.contractType == DD_MANDATE)
    assert(contractUnSigned.format == PDF)
    assert(contractUnSigned.name == "Direct Debit Mandate"++ddMandate.identity.toString)
    assert(contractUnSigned.reference == ddMandate.identity.uuid.toString)
    assert(contractUnSigned.file == fakeFileRepository)
  }

  test("sign a contract"){

    val unsignedContract = buildUnsignedContract()

    val signatureDate = new Date()
    val signedFile = fakeFileRepository
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

}