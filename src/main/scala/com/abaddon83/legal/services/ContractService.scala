package com.abaddon83.legal.services

import java.util.Date

import com.abaddon83.legal.domainModel.contract.{Contract, ContractIdentity, ContractUnSigned}
import com.abaddon83.legal.domainModel.contract.Repositories.Repository
import com.abaddon83.legal.domainModel.ddMandates.DDMandate
import com.abaddon83.legal.ports.ContractRepositoryPort

class ContractService(
  repository: ContractRepositoryPort
) {

  def createDDMandateContract(ddMandate : DDMandate, unsignedFile: Repository): ContractUnSigned ={

    val contractUnSigned = ContractUnSigned(ddMandate,unsignedFile)
    repository.save(contractUnSigned)

  }

  def signContract(contractIdentity: ContractIdentity, signedFile: Repository, signedDate: Date): Contract = {

    val contractUnsigned = repository.findByContractUnSignedIdentity(contractIdentity)

    val contractSigned = contractUnsigned.sign(signedFile,signedDate)

    repository.save(contractSigned)

  }



}
