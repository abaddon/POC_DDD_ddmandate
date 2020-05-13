package com.abaddon83.legal.contracts.ports

import com.abaddon83.legal.contracts.domainModels.{Contract, ContractSigned, ContractUnSigned}
import com.abaddon83.legal.sharedValueObjects.contracts.ContractIdentity

trait ContractRepositoryPort {

  def save(contract: ContractUnSigned): ContractUnSigned
  def save(contract: ContractSigned): ContractSigned
  def findContractByIdentity(contractIdentity: ContractIdentity): Option[Contract]
  def findContractUnSignedByIdentity(contractIdentity: ContractIdentity): Option[ContractUnSigned]
  def findContractSignedByIdentity(contractIdentity: ContractIdentity): Option[ContractSigned]
}
