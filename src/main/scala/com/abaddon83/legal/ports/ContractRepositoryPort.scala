package com.abaddon83.legal.ports

import com.abaddon83.legal.domainModel.contract.{Contract, ContractIdentity, ContractSigned, ContractUnSigned}

trait ContractRepositoryPort {

  def save(contract: ContractUnSigned): ContractUnSigned
  def save(contract: ContractSigned): ContractSigned
  def findByContractUnSignedIdentity(contractIdentity: ContractIdentity): ContractUnSigned
  def findByContractSignedIdentity(contractIdentity: ContractIdentity): ContractSigned
}
