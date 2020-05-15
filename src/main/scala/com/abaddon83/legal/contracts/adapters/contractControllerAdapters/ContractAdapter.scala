package com.abaddon83.legal.contracts.adapters.contractControllerAdapters

import java.util.{Date, UUID}

import com.abaddon83.legal.contracts.domainModels.FileRepositories.FileRepository
import com.abaddon83.legal.contracts.domainModels.{Contract, ContractSigned, ContractUnSigned}
import com.abaddon83.legal.contracts.ports.{ContractPort, ContractRepositoryPort, DDMandatePort, FileRepositoryPort}
import com.abaddon83.legal.contracts.services.ContractService
import com.abaddon83.legal.sharedValueObjects.contracts.ContractIdentity
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity
import wvlet.airframe._

import scala.concurrent.Future


trait ContractAdapter extends ContractPort{

  val ddMandatePort = bind[DDMandatePort]
  val contractRepositoryPort = bind[ContractRepositoryPort]
  val fileRepositoryPort = bind[FileRepositoryPort]

  private lazy val contractService: ContractService = new ContractService(contractRepositoryPort,fileRepositoryPort,ddMandatePort)

  override def createContract(contractType: String, reference: UUID): Future[ContractUnSigned] = {
      contractType match {
        case "DDMANDATE" => contractService.createDDMandateContract(DDMandateIdentity(reference))
        case "T&C" => contractService.createTCContract()
        case _   => throw new UnsupportedOperationException(s"Cannot possible create a Contract with type: ${contractType}, types available: DDMANDATE and T&C")
      }
  }

  override def findByIdContract(contractId: UUID): Future[Contract] = {
      contractService.search().findContractByIdentity(ContractIdentity(contractId))
  }

  override def signContract(contractId: UUID, file: FileRepository, signatureDate: Date ): Future[ContractSigned] = {
      contractService.signContract(ContractIdentity(contractId), file, signatureDate)
  }

}
