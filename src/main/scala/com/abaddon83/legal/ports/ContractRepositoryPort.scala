package com.abaddon83.legal.ports

import com.abaddon83.legal.domainModel.contract.{Contract, ContractIdentity}

trait ContractRepositoryPort {

  def save(contract: Contract): Contract
  def findByContractIdentity(contractIdentity: ContractIdentity): Contract
}
