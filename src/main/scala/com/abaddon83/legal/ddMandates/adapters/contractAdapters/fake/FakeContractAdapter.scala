package com.abaddon83.legal.ddMandates.adapters.contractAdapters.fake

import java.util.Date

import com.abaddon83.legal.ddMandates.domainModels.{Contract, ContractSigned, ContractUnSigned, DDMandate}
import com.abaddon83.legal.ddMandates.ports.ContractPort
import com.abaddon83.legal.sharedValueObjects.contracts.{ContractIdentity, DD_MANDATE, PDF}

import scala.collection.mutable.ListBuffer

class FakeContractAdapter extends ContractPort{
  override def findSignedContractByContractId(contractIdentity: ContractIdentity): Option[ContractSigned] = {
    repository.db.find(contract => contract.identity == contractIdentity) match {
      case Some(value: ContractSigned) => Some(value)
      case _  => None
    }
  }

  override def createContract(ddMandate: DDMandate): Option[ContractUnSigned] = {
    val contract = ContractUnSigned.apply(ContractIdentity(),ddMandate.identity.uuid.toString,DD_MANDATE,"DD mandate contract",PDF,new Date())
    persist(contract)
    Some(contract)
  }

  /*test purpose*/
  def setSigned(contractIdentity: ContractIdentity) ={
    repository.db.find(contract => contract.identity == contractIdentity) match {
      case Some(contractUnSigned: ContractUnSigned) => update(contractUnSigned,signContract(contractUnSigned))
    }
  }


  private def signContract(contract: ContractUnSigned): ContractSigned = {
    ContractSigned(contract.identity,contract.reference,contract.contractType,contract.name,contract.format,contract.creationDate,new Date())
  }



  private object repository{
    var db: ListBuffer[Contract]= ListBuffer()
  }

  private def persist(contract: Contract) = {
    repository.db = repository.db.addOne(contract)

    //debug()
  }

  private def update(oldContract: Contract,newContract: Contract) = {
    repository.db = repository.db-=oldContract
    persist(newContract)
  }


  private def debug(): Unit ={
    println(s"Contract Repository size: ${repository.db.size}")
    println("---start---")
    repository.db.foreach(contract => {
      println(s"ID: ${contract.identity.uuid.toString}")
      println(s"--- Reference: ${contract.reference}")
      contract match {
        case ContractUnSigned(identity, reference, contractType, name, format, creationDate) => println(s"--- Type: UNSIGNED")
        case ContractSigned(identity, reference, contractType, name, format, creationDate, signatureDate) => println(s"--- Type: SIGNED")
      }
    })
    println("---end---")
  }

}
