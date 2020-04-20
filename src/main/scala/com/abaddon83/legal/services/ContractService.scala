package com.abaddon83.legal.services

import java.util.Date

import com.abaddon83.legal.domainModel.contract.{Contract, ContractIdentity}
import com.abaddon83.legal.domainModel.contract.Repositories.Repository
import com.abaddon83.legal.domainModel.ddMandates.DDMandate
import com.abaddon83.legal.ports.ContractRepositoryPort

class ContractService(
  repository: ContractRepositoryPort
) {

  def createDDMandateContract(ddMandate : DDMandate): Contract ={

    val contract: Contract = Contract(ddMandate)
    repository.save(contract)

  }

  def signContract(contractIdentity: ContractIdentity, signedFile: Repository, signedDate: Date): Contract = {

    val contract: Contract = repository.findByContractIdentity(contractIdentity)

    val contractSigned = contract.sign(signedFile,signedDate)

    repository.save(contractSigned)

  }



}
