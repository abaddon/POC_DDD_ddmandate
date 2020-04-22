package com.abaddon83.legal.ports

import com.abaddon83.legal.domainModel.contract.{Contract, ContractIdentity, ContractSigned, ContractUnSigned}

trait ContractRepositoryPort {

  def save(contract: ContractUnSigned): ContractUnSigned
  def save(contract: ContractSigned): ContractSigned
  def findByContractUnSignedByIdentity(contractIdentity: ContractIdentity): Option[ContractUnSigned]
  def findByContractSignedByIdentity(contractIdentity: ContractIdentity): Option[ContractSigned]
}
