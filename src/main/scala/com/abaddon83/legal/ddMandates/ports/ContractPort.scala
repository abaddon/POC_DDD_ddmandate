package com.abaddon83.legal.ddMandates.ports

import com.abaddon83.legal.ddMandates.domainModels.{ContractSigned, ContractUnSigned, DDMandate}
import com.abaddon83.legal.sharedValueObjects.contracts.ContractIdentity

trait ContractPort {
  def findSignedContractByContractId(contractIdentity: ContractIdentity): Option[ContractSigned]

  def createContract(ddMandate: DDMandate): Option[ContractUnSigned]
}
