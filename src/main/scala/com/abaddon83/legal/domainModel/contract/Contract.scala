package com.abaddon83.legal.domainModel.contract

import java.util.Date

import com.abaddon83.legal.domainModel.contract.Repositories.{Repository, S3Repository}
import com.abaddon83.legal.domainModel.ddMandates.{DDMandate, DRAFT}

case class Contract (
      identity: ContractIdentity,
      contractType:ContractType,
      reference: String,
      name: String,
      format: Format,
      file: Repository,
      creationDate: Date,
      var signedFile:Option[Repository],
      var signatureDate: Option[Date]
){
  def sign(signedFile: Repository, signedDate: Date): Contract ={
    //PRE
    assert(!isSigned(),"Contract is already signed, cannot sign again")
    assert(signatureDate.isEmpty,"The signature date is already defined")

    this.signedFile = Some(signedFile)
    this.signatureDate = Some(signedDate)

    //POST
    assert(isSigned(),"Contract should be signed")
    assert(signatureDate.isDefined,"Signature Date should be defined")
    assert(this.signedFile.isDefined,"Signature Date should be defined")

    this
  }

  def isSigned() : Boolean = {
    signedFile.isDefined
  }
}

object Contract{
  def apply(ddMandate : DDMandate): Contract = {
    //PRE
    assert(ddMandate.status == DRAFT, "DDMandate has to be in status draft")

    val repository = S3Repository("key","container")
    val contract  = new Contract(ContractIdentity(),DD_MANDATE, ddMandate.identity.toString, DD_MANDATE.name(Some(ddMandate.identity.toString)), DD_MANDATE.format,repository, new Date(),None,None)

    //POST
    assert(contract.contractType == DD_MANDATE,"The contract should have type DD_MANDATE")
    assert(contract.reference == ddMandate.identity.toString, "The reference and the DD mandate id are different")
    assert(!contract.name.isEmpty, "The contract name cannot be empty")
    assert(contract.format == PDF, "The contract format has to be a pdf")
    //assert(contract.creationDate.before(new Date())) //TO FIX)

    contract
  }
}
