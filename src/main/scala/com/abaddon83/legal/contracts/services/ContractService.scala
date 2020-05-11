package com.abaddon83.legal.contracts.services

import java.util.Date

import com.abaddon83.legal.contracts.domainModels.{Contract, ContractUnSigned, DDMandate}
import com.abaddon83.legal.contracts.domainModels.FileRepositories.FileRepository
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, FileRepositoryPort}
import com.abaddon83.legal.sharedValueObjects.contracts.ContractIdentity

class ContractService(
  repository: ContractRepositoryPort,
  fileRepository: FileRepositoryPort
) {

  def createDDMandateContract(ddMandate : DDMandate): ContractUnSigned ={

    val unsignedFile = fileRepository.createUnsignedDDMandate(ddMandate)
    val contractUnSigned = unsignedFile match {
      case unsignedFile: FileRepository => ContractUnSigned(ddMandate,unsignedFile)
      case None => throw new NoSuchElementException("Unsigned file not created")
    }

    repository.save(contractUnSigned)
  }

  def signContract(contractIdentity: ContractIdentity, signedFile: FileRepository, signedDate: Date): Contract = {

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
