package com.abaddon83.legal.domainModel.adapters

import java.util.{Date, UUID}

import com.abaddon83.legal.domainModel.contract.FileRepositories.FileRepository
import com.abaddon83.legal.domainModel.contract.{Contract, ContractIdentity, ContractSigned, ContractUnSigned, DD_MANDATE, PDF}
import com.abaddon83.legal.ports.ContractRepositoryPort

import scala.collection.mutable.ListBuffer

class FakeContractRepositoryAdapter extends ContractRepositoryPort{

  override def save(contract: ContractUnSigned): ContractUnSigned = {
    saveContract(contract)
    contract
  }

  override def save(contract: ContractSigned): ContractSigned = {
    saveContract(contract)
    contract
  }

  override def findByContractUnSignedByIdentity(contractIdentity: ContractIdentity): Option[ContractUnSigned] = {
    repository.db.find(contract => contract.identity == contractIdentity && !contract.isSigned).asInstanceOf[Option[ContractUnSigned]]
  }

  override def findByContractSignedByIdentity(contractIdentity: ContractIdentity): Option[ContractSigned] = {
    repository.db.find(contract => contract.identity == contractIdentity && contract.isSigned).asInstanceOf[Option[ContractSigned]]
  }

  private def saveContract(newContract: Contract) = {

    repository.db.find(contract => contract.identity == newContract.identity) match {
      case Some(existingContract) => update(existingContract,newContract)
      case None =>persist(newContract)
    }
  }

  private object repository{
    var db: ListBuffer[Contract]= ListBuffer(
      new ContractUnSigned(ContractIdentity(UUID.fromString("1469e8b0-7b98-4755-96b4-c3efea1a5894")),DD_MANDATE,"6bde5e29-f9d9-443a-9c86-66af81879383","fake dd mandate",PDF,fakeFileRepository,new Date())
    )
  }

  private def persist(contract: Contract) = {
    repository.db = repository.db.addOne(contract)

    //debug()
  }

  private def update(oldContract: Contract,newContract: Contract) = {
    repository.db = repository.db-=oldContract
    persist(newContract)
  }

  private object fakeFileRepository extends FileRepository {
    override val provider: String = "Fake"

    override def url: String = "ddmandate.contract.test"
  }

  private def debug(): Unit ={
    println(s"Contract Repository size: ${repository.db.size}")
    println("---start---")
    repository.db.foreach(contract => {
      println(s"contract id: ${contract.identity.uuid.toString} SIGNATURE STATUS: ${contract.isSigned}")
    })
    println("---end---")
  }

}
