package com.abaddon83.legal.domainModel.contract

import java.util.{Date, UUID}

import com.abaddon83.ddd.Entity
import com.abaddon83.legal.domainModel.contract.FileRepositories.FileRepository
import com.abaddon83.legal.domainModel.ddMandates.{DDMandate}

sealed trait Contract extends Entity{
  val identity: ContractIdentity
  val contractType:ContractType
  val reference: String
  val name: String
  val format: Format
  val file: FileRepository
  val creationDate: Date
}



//CONTRACT UNSIGNED

case class ContractUnSigned(
                             identity: ContractIdentity,
                             contractType:ContractType,
                             reference: String,
                             name: String,
                             format: Format,
                             file: FileRepository,
                             creationDate: Date
      ) extends Contract {

  def sign(signedFile: FileRepository, signatureDate: Date): ContractSigned ={

    ContractSigned(this,signedFile,signatureDate)
  }


}

object ContractUnSigned extends Entity{
  def apply(ddMandate : DDMandate, unsignedfile: Option[FileRepository]): ContractUnSigned = {
    //PRE
    assertArgumentNotEmpty(unsignedfile,"document file is missing")

    val contractUnsigned  = new ContractUnSigned(ContractIdentity(),DD_MANDATE, ddMandate.identity.uuid.toString, DD_MANDATE.name(Some(ddMandate.identity.toString)), DD_MANDATE.format,unsignedfile.get, new Date())

    //POST
    assert(contractUnsigned.contractType == DD_MANDATE,"The contract should have type DD_MANDATE")
    assert(UUID.fromString(contractUnsigned.reference).isInstanceOf[UUID])
    assert(contractUnsigned.reference == ddMandate.identity.uuid.toString, "The reference and the DD mandate id are different")
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
                           file: FileRepository,
                           creationDate: Date,
                           signedFile: FileRepository,
                           signatureDate: Date) extends Contract {

}

object ContractSigned extends Entity{
  def apply(contractUnSigned: ContractUnSigned, signedFile: FileRepository, signatureDate: Date): ContractSigned = {

    val contractSigned = new ContractSigned(contractUnSigned.identity,contractUnSigned.contractType,contractUnSigned.reference,contractUnSigned.name,contractUnSigned.format,contractUnSigned.file,contractUnSigned.creationDate,signedFile,signatureDate)

    //POST
    assert(contractSigned.identity == contractUnSigned.identity)
    assert(contractSigned.contractType == contractUnSigned.contractType)
    assert(contractSigned.creationDate == contractUnSigned.creationDate)
    assert(contractSigned.file == contractUnSigned.file)
    assert(contractSigned.format == contractUnSigned.format)
    assert(contractSigned.name == contractUnSigned.name)
    assert(UUID.fromString(contractSigned.reference).isInstanceOf[UUID])
    assert(contractSigned.reference == contractUnSigned.reference)
    assert(contractSigned.signedFile == signedFile)
    assert(contractSigned.signatureDate == signatureDate)

    contractSigned

  }
}