package com.abaddon83.legal.contracts.services

import java.util.Date

import com.abaddon83.legal.contracts.domainModels.FileRepositories.FileRepository
import com.abaddon83.legal.contracts.domainModels.{ContractSigned, ContractUnSigned}
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, DDMandatePort, FileRepositoryPort}
import com.abaddon83.legal.sharedValueObjects.contracts.ContractIdentity
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity

class ContractService(
  repository: ContractRepositoryPort,
  fileRepository: FileRepositoryPort,
  ddMandatePort: DDMandatePort
) {

  def createDDMandateContract(ddMandateIdentity: DDMandateIdentity): ContractUnSigned ={

    val ddMandate = ddMandatePort.findDDMandateById(ddMandateIdentity) match {
      case Some(value) => value
      case None => throw new IllegalArgumentException(s"DD Mandate with id ${ddMandateIdentity.uuid} not found")
    }
    val unsignedFile = fileRepository.createUnsignedDDMandate(ddMandate) match {
      case Some(value) => value
      case None => throw new NoSuchElementException("Unsigned file not created")
    }

    val contractUnSigned = ContractUnSigned(ddMandate,unsignedFile)

    repository.save(contractUnSigned)
  }

  def createTCContract(): ContractUnSigned = {
    throw new NotImplementedError("T&C Contract not implemented")
  }

  def signContract(contractIdentity: ContractIdentity, signedFile: FileRepository, signedDate: Date): ContractSigned = {

    val contractUnsigned = repository.findByContractUnSignedByIdentity(contractIdentity) match {
      case Some(value) => value
      case None => throw new IllegalArgumentException("Unsigned Contract with id: "++contractIdentity.toString++" not found")
    }

    val contractSigned = contractUnsigned.sign(signedFile,signedDate)

    repository.save(contractSigned)

  }

  def search(): ContractRepositoryPort = {
    this.repository
  }



}
