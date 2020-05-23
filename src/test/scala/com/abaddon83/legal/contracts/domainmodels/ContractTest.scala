package com.abaddon83.legal.contracts.domainmodels

import java.util.Date

import com.abaddon83.legal.contracts.domainModels.ContractUnSigned
import com.abaddon83.legal.contracts.utilities.ContractDomainElementHelper
import com.abaddon83.legal.shares.contracts.{DD_MANDATE, Format}
import org.scalatest.funsuite.AnyFunSuite


class ContractTest extends AnyFunSuite with ContractDomainElementHelper{

  test("New dd mandate contract created") {

    val ddMandate = buildDDMandate()

    val contractUnSigned = ContractUnSigned(ddMandate,fakeFileRepository)

    assert(contractUnSigned.contractType == DD_MANDATE)
    assert(contractUnSigned.format == Format.PDF)
    assert(contractUnSigned.name == s"DD_MANDATE ${ddMandate.identity.toString}")
    assert(contractUnSigned.reference == ddMandate.identity.convertTo().toString)
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