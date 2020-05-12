package com.abaddon83.legal.contracts.adapters.contractAdapters.akkaHttp

import java.util.{Date, UUID}

import com.abaddon83.legal.contracts.domainModels.FileRepositories.FileRepository
import com.abaddon83.legal.contracts.domainModels.{Contract, ContractSigned, ContractUnSigned}
import com.abaddon83.legal.contracts.ports.{ContractPort, DIPortsBinding}
import com.abaddon83.legal.contracts.services.ContractService
import com.abaddon83.legal.sharedValueObjects.contracts.ContractIdentity
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class ContractAdapter extends ContractPort with DIPortsBinding{

  private lazy val contractService: ContractService = new ContractService(contractRepositoryPort,fileRepositoryPort,ddMandatePort)

  override def createContract(contractType: String, reference: UUID): Future[ContractUnSigned] = {
    Future{
      contractType match {
        case "DDMANDATE" => contractService.createDDMandateContract(DDMandateIdentity(reference))
        case "T&C" => contractService.createTCContract()
        case _   => throw new UnsupportedOperationException(s"Cannot possible create a Contract with type: ${contractType}, types available: DDMANDATE and T&C")
      }
    }
  }

  override def findByIdContract(contractId: UUID): Future[Contract] = {
    Future{
      contractService.search().findByContractSignedByIdentity(ContractIdentity(contractId)) match {
        case Some(value) => value
        case None => throw new NoSuchElementException(s"Contract with id: ${contractId.toString} not found ")
      }
    }
  }

  override def signContract(contractId: UUID, file: FileRepository, signatureDate: Date ): Future[ContractSigned] = {
    Future{
      contractService.signContract(ContractIdentity(contractId), file, signatureDate)
    }
  }
}
