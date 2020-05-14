package com.abaddon83.legal.ddMandates.ports

import com.abaddon83.legal.ddMandates.domainModels.{DDMandate, DDMandateContract}
import com.abaddon83.legal.sharedValueObjects.contracts.ContractIdentity

import scala.concurrent.Future

trait DDMandateContractPort {
  def findSignedContractByContractId(contractIdentity: ContractIdentity): Future[DDMandateContract]

  def createContract(ddMandate: DDMandate): Future[DDMandateContract]
}
