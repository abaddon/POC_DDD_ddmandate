package com.abaddon83.legal.ddMandates.adapters.ddMandateContractAdapters.fake

import java.util.Date

import com.abaddon83.legal.ddMandates.domainModels.{DDMandate, DDMandateContract}
import com.abaddon83.legal.ddMandates.ports.DDMandateContractPort
import com.abaddon83.legal.sharedValueObjects.contracts.{ContractIdentity, DD_MANDATE, PDF}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class FakeDDMandateContractAdapter extends DDMandateContractPort{

  override def findSignedContractByContractId(contractIdentity: ContractIdentity): Future[DDMandateContract] = {
    Future{
      repository.db.find(contract => contract.identity == contractIdentity)
        .filter(_.isSigned)
        .getOrElse(throw new NoSuchElementException(s"Contract signed with id: ${contractIdentity.toString} not found"))
    }

  }

  override def createContract(ddMandate: DDMandate): Future[DDMandateContract] = {
    Future{
      val contract = DDMandateContract.apply(ContractIdentity(),ddMandate.identity.uuid.toString,DD_MANDATE,"DD mandate contract",PDF,new Date(),None)
      persist(contract)
      contract
    }
  }

  /*test purpose*/
  def setSigned(contractIdentity: ContractIdentity) ={
    repository.db.find(contract => contract.identity == contractIdentity) match {
      case Some(contractUnSigned: DDMandateContract) => update(contractUnSigned,signContract(contractUnSigned))
    }
  }


  private def signContract(contract: DDMandateContract): DDMandateContract = {
    contract.copy(signatureDate = Some(new Date()))
  }



  private object repository{
    var db: ListBuffer[DDMandateContract]= ListBuffer()
  }

  private def persist(contract: DDMandateContract) = {
    repository.db = repository.db.addOne(contract)

    //debug()
  }

  private def update(oldContract: DDMandateContract,newContract: DDMandateContract) = {
    repository.db = repository.db-=oldContract
    persist(newContract)
  }


  private def debug(): Unit ={
    println(s"Contract Repository size: ${repository.db.size}")
    println("---start---")
    repository.db.foreach(contract => {
      println(s"ID: ${contract.identity.uuid.toString}")
      println(s"--- Reference: ${contract.reference}")
      println(s"--- SIGNED: ${contract.isSigned}")
    })
    println("---end---")
  }

}
