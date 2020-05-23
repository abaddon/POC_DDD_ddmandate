package com.abaddon83.legal.contracts.services

import java.util.Date

import com.abaddon83.legal.contracts.domainModels.FileRepositories.FileRepository
import com.abaddon83.legal.contracts.domainModels.{ContractSigned, ContractUnSigned, DDMandate}
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, DDMandatePort, FileDocumentPort}
import com.abaddon83.legal.shares.contracts.{ContractIdentity, Format}
import com.abaddon83.legal.shares.ddMandates.DDMandateIdentity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class ContractService(
                       repository: ContractRepositoryPort,
                       documentPort: FileDocumentPort,
                       ddMandatePort: DDMandatePort
  ) {

  def createDDMandateContract(ddMandateIdentity: DDMandateIdentity): Future[ContractUnSigned] ={
    val ddMandate=DDMandate(ddMandateIdentity)
    for{
      document <- documentPort.createDocument(ddMandate, Format.PDF)
    } yield repository.save(ContractUnSigned(ddMandate,document))

    /*val ddMandate = ddMandatePort.findDDMandateById(ddMandateIdentity) match {
      case Some(value) => value
      case None => throw new IllegalArgumentException(s"DD Mandate with id ${ddMandateIdentity.uuid} not found")
    }
     */
    /*
    val unsignedFile = fileRepository.createUnsignedDDMandate(ddMandate) match {
      case Some(value) => value
      case None => throw new NoSuchElementException("Unsigned file not created")
    }
    */
    //val contractUnSigned = ContractUnSigned(ddMandate,unsignedFile)

    //repository.save(contractUnSigned)
  }

  def createTCContract(): Future[ContractUnSigned] = {
    throw new NotImplementedError("T&C Contract not implemented")
  }

  def signContract(contractIdentity: ContractIdentity, signedFile: FileRepository, signedDate: Date): Future[ContractSigned] = {
    for{
      contractUnsigned <- repository.findContractUnSignedByIdentity(contractIdentity)
    } yield repository.save(contractUnsigned.sign(signedFile,signedDate))

    /*val contractUnsigned = repository.findContractUnSignedByIdentity(contractIdentity) match {
      case Some(value) => value
      case None => throw new IllegalArgumentException("Unsigned Contract with id: "++contractIdentity.toString++" not found")
    }*/

    /*val contractSigned =
    repository.save(contractSigned)
    */
  }

  def search(): ContractRepositoryPort = {
    this.repository
  }



}
