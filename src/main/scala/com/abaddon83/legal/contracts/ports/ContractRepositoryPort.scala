package com.abaddon83.legal.contracts.ports

import com.abaddon83.legal.contracts.domainModels.{ContractSigned, ContractUnSigned}
import com.abaddon83.legal.sharedValueObjects.contracts.ContractIdentity

trait ContractRepositoryPort {

  def save(contract: ContractUnSigned): ContractUnSigned
  def save(contract: ContractSigned): ContractSigned
  def findByContractUnSignedByIdentity(contractIdentity: ContractIdentity): Option[ContractUnSigned]
  def findByContractSignedByIdentity(contractIdentity: ContractIdentity): Option[ContractSigned]
}
