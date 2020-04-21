package com.abaddon83.legal.domainModel.contract

import java.util.Date

import com.abaddon83.legal.domainModel.contract.Repositories.{Repository, S3Repository}
import com.abaddon83.legal.domainModel.ddMandates.{DDMandate, DRAFT}

sealed trait Contract{
  val identity: ContractIdentity
  val contractType:ContractType
  val reference: String
  val name: String
  val format: Format
  val file: Repository
  val creationDate: Date
  def isSigned: Boolean
}



//CONTRACT UNSIGNED

case class ContractUnSigned(
      identity: ContractIdentity,
      contractType:ContractType,
      reference: String,
      name: String,
      format: Format,
      file: Repository,
      creationDate: Date,
      ) extends Contract {
  override def isSigned: Boolean = false


  def sign(signedFile: Repository, signatureDate: Date): ContractSigned ={

    val contractSigned = ContractSigned(this,signedFile,signatureDate)

    assert(contractSigned.signedFile == signedFile)
    assert(contractSigned.signatureDate == signatureDate)

    contractSigned
  }


}

object ContractUnSigned{
  def apply(ddMandate : DDMandate, unsignedfile: Repository): ContractUnSigned = {
    //PRE
    assert(ddMandate.status == DRAFT, "DDMandate has to be in status draft")

    val contractUnsigned  = new ContractUnSigned(ContractIdentity(),DD_MANDATE, ddMandate.identity.toString, DD_MANDATE.name(Some(ddMandate.identity.toString)), DD_MANDATE.format,unsignedfile, new Date())

    //POST
    assert(contractUnsigned.contractType == DD_MANDATE,"The contract should have type DD_MANDATE")
    assert(contractUnsigned.reference == ddMandate.identity.toString, "The reference and the DD mandate id are different")
    assert(contractUnsigned.name.length > 0, "The contract name cannot be empty")
    assert(contractUnsigned.format == PDF, "The contract format has to be a pdf")

    contractUnsigned
  }
}

//CONTRACT SIGNED

case class ContractSigned(
                             identity: ContractIdentity,
                             contractType:ContractType,
                             reference: String,
                             name: String,
                             format: Format,
                             file: Repository,
                             creationDate: Date,
                             signedFile: Repository,
                             signatureDate: Date) extends Contract {
  override def isSigned: Boolean = true

}

object ContractSigned {
  def apply(contractUnSigned: ContractUnSigned, signedFile: Repository, signatureDate: Date): ContractSigned = {
    val contractSigned = new ContractSigned(contractUnSigned.identity,contractUnSigned.contractType,contractUnSigned.reference,contractUnSigned.name,contractUnSigned.format,contractUnSigned.file,contractUnSigned.creationDate,signedFile,signatureDate)

    assert(contractSigned.identity == contractUnSigned.identity)
    assert(contractSigned.contractType == contractUnSigned.contractType)
    assert(contractSigned.creationDate == contractUnSigned.creationDate)
    assert(contractSigned.file == contractUnSigned.file)
    assert(contractSigned.format == contractUnSigned.format)
    assert(contractSigned.name == contractUnSigned.name)
    assert(contractSigned.reference == contractUnSigned.reference)
    assert(contractSigned.signedFile == signedFile)
    assert(contractSigned.signatureDate == signatureDate)

    contractSigned

  }
}