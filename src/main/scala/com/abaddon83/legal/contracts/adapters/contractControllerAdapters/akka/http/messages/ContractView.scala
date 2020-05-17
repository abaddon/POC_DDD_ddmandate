package com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.http.messages

import java.util.{Date, UUID}

import com.abaddon83.legal.contracts.domainModels.FileRepositories.FileRepository
import com.abaddon83.legal.contracts.domainModels.{Contract, ContractSigned, ContractUnSigned}


case class ContractView(id: UUID, status: String, contractType: String, reference: String, name: String, format: String, file: FileRepositoryView, creationDate: Date, signedFile: Option[FileRepositoryView], signatureDate: Option[Date])

case class FileRepositoryView(provider: String, url: String){

}


object ContractView{
  def apply(contract: Contract):ContractView = {
    contract match {
      case contractUnsigned: ContractUnSigned => convertTo(contractUnsigned)
      case contractSigned: ContractSigned => convertTo(contractSigned)
      case _ => throw new IllegalArgumentException(s"Unrecognised status of Contract ${contract.identity.uuid}")
    }
  }

  private def convertTo(contract: ContractUnSigned): ContractView = {
    new ContractView(contract.identity.uuid,"unsigned",contract.contractType.toString,contract.reference,contract.name,contract.format.toString,FileRepositoryView(contract.file),contract.creationDate,None,None)
  }

  private def convertTo(contract: ContractSigned): ContractView = {
    new ContractView(contract.identity.uuid,"signed",contract.contractType.toString,contract.reference,contract.name,contract.format.toString,FileRepositoryView(contract.file),contract.creationDate,Some(FileRepositoryView(contract.signedFile)),Some(contract.signatureDate))
  }
}
object FileRepositoryView{
  def apply(fileRepository: FileRepository): FileRepositoryView ={
    new FileRepositoryView(fileRepository.provider,fileRepository.url)
  }
}
