package com.abaddon83.legal.services

import java.util.Date

import com.abaddon83.legal.domainModel.contract.{Contract, ContractIdentity, ContractUnSigned}
import com.abaddon83.legal.domainModel.contract.FileRepositories.FileRepository
import com.abaddon83.legal.domainModel.ddMandates.DDMandate
import com.abaddon83.legal.ports.{ContractRepositoryPort, FileRepositoryPort}

class ContractService(
  repository: ContractRepositoryPort,
  fileRepository: FileRepositoryPort,
) {

  def createDDMandateContract(ddMandate : DDMandate): ContractUnSigned ={

    val unsignedFile = fileRepository.createUnsignedDDMandate(ddMandate)

    val contractUnSigned = ContractUnSigned(ddMandate,unsignedFile)
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



}
