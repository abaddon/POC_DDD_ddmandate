package com.abaddon83.legal.contracts.ports

import com.abaddon83.legal.contracts.domainModels.{Contract, ContractSigned, ContractUnSigned}
import com.abaddon83.legal.sharedValueObjects.contracts.ContractIdentity

import scala.concurrent.Future

trait ContractRepositoryPort {

  def save(contract: ContractUnSigned): ContractUnSigned
  def save(contract: ContractSigned): ContractSigned
  def findContractByIdentity(contractIdentity: ContractIdentity): Future[Contract]
  def findContractUnSignedByIdentity(contractIdentity: ContractIdentity): Future[ContractUnSigned]
  def findContractSignedByIdentity(contractIdentity: ContractIdentity): Future[ContractSigned]
}
