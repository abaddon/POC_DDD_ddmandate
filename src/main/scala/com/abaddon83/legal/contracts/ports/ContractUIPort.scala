package com.abaddon83.legal.contracts.ports

import java.util.{Date, UUID}

import com.abaddon83.legal.contracts.domainModels.FileRepositories.FileRepository
import com.abaddon83.legal.contracts.domainModels.{Contract, ContractSigned, ContractUnSigned}

import scala.concurrent.Future

trait ContractUIPort {
  def createContract(contractType: String, reference: UUID): Future[ContractUnSigned]
  def findByIdContract(contractId: UUID): Future[Contract]
  def findSignedContractByIdContract(contractId: UUID): Future[ContractSigned]
  def signContract(contractId: UUID, file: FileRepository, signatureDate: Date ): Future[ContractSigned]
}
