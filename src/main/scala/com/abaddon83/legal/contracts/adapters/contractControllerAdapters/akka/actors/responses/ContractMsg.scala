package com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors.responses

import java.util.{Date, UUID}

import com.abaddon83.legal.contracts.domainModels.FileRepositories.FileRepository
import com.abaddon83.legal.contracts.domainModels.{Contract, ContractSigned, ContractUnSigned}
//sealed trait ResponseMsg
final case class ContractMsg(id: UUID, status: String, contractType: String, reference: String, name: String, format: String, file: FileRepositoryView, creationDate: Date, signedFile: Option[FileRepositoryView], signatureDate: Option[Date]) //extends ResponseMsg

case class FileRepositoryView(provider: String, url: String){

}


object ContractMsg{
  def apply(contract: Contract):ContractMsg = {
    println(s"Creating ContractMsg: ${contract.identity}")
    contract match {
      case contractUnsigned: ContractUnSigned => convertTo(contractUnsigned)
      case contractSigned: ContractSigned => convertTo(contractSigned)
      case _ => throw new IllegalArgumentException(s"Unrecognised status of Contract ${contract.identity}")
    }
  }

   def convertTo(contract: ContractUnSigned): ContractMsg = {
    new ContractMsg(contract.identity.convertTo(),"unsigned",contract.contractType.toString,contract.reference,contract.name,contract.format.toString,FileRepositoryView(contract.file),contract.creationDate,None,None)
  }

   def convertTo(contract: ContractSigned): ContractMsg = {
    new ContractMsg(contract.identity.convertTo(),"signed",contract.contractType.toString,contract.reference,contract.name,contract.format.toString,FileRepositoryView(contract.file),contract.creationDate,Some(FileRepositoryView(contract.signedFile)),Some(contract.signatureDate))
  }
}

object FileRepositoryView{
  def apply(fileRepository: FileRepository): FileRepositoryView ={
    new FileRepositoryView(fileRepository.provider,fileRepository.url)
  }
}
