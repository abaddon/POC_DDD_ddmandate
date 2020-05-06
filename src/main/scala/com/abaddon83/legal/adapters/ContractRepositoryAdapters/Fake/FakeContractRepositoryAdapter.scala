package com.abaddon83.legal.adapters.ContractRepositoryAdapters.Fake

import java.util.{Date, UUID}

import com.abaddon83.legal.domainModel.contract.FileRepositories.FileRepository
import com.abaddon83.legal.domainModel.contract._
import com.abaddon83.legal.ports.ContractRepositoryPort

import scala.collection.mutable.ListBuffer

class FakeContractRepositoryAdapter extends ContractRepositoryPort{

  override def save(contract: ContractUnSigned): ContractUnSigned = {

    saveContract(ContractRepo.Apply(contract))
    contract
  }

  override def save(contract: ContractSigned): ContractSigned = {
    saveContract(ContractRepo.Apply(contract))
    contract
  }

  override def findByContractUnSignedByIdentity(contractIdentity: ContractIdentity): Option[ContractUnSigned] = {
    repository.db.find(contractRepo => contractRepo.identity == contractIdentity && contractRepo.status == UNSIGNED)
      .map(_.buildContract().asInstanceOf[ContractUnSigned])

  }

  override def findByContractSignedByIdentity(contractIdentity: ContractIdentity): Option[ContractSigned] = {
    repository.db.find(contractRepo => contractRepo.identity == contractIdentity && contractRepo.status == SIGNED)
      .map(_.buildContract().asInstanceOf[ContractSigned])
  }

  private def saveContract(newContract: ContractRepo) = {

    repository.db.find(contract => contract.identity == newContract.identity) match {
      case Some(existingContract) => update(existingContract,newContract)
      case None =>persist(newContract)
    }
  }

  private object repository{
    var db: ListBuffer[ContractRepo]= ListBuffer(
      ContractRepo.Apply(new ContractUnSigned(ContractIdentity(UUID.fromString("1469e8b0-7b98-4755-96b4-c3efea1a5894")),DD_MANDATE,"6bde5e29-f9d9-443a-9c86-66af81879383","fake dd mandate",PDF,fakeFileRepository,new Date()))
    )
  }

  private def persist(contract: ContractRepo) = {
    repository.db = repository.db.addOne(contract)

    //debug()
  }

  private def update(oldContract: ContractRepo,newContract: ContractRepo) = {
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
    repository.db.foreach(contractRepo => {
      println(s"ID: ${contractRepo.identity.uuid.toString}")
      println(s"--- STATUS: ${contractRepo.status}")
      println(s"--- ContractType: ${contractRepo.contractType}")
      println(s"--- Reference: ${contractRepo.reference}")
      println(s"--- Name: ${contractRepo.name}")
      println(s"--- Format: ${contractRepo.format}")
      println(s"--- File: ${contractRepo.file}")
      println(s"--- CreationDate: ${contractRepo.creationDate}")
      println(s"--- signedFile: ${contractRepo.signedFile}")
      println(s"--- signatureDate: ${contractRepo.signatureDate}")
    })
    println("---end---")
  }

}

protected case class ContractRepo(
                                     identity: ContractIdentity,
                                     status: ContractStatus,
                                     contractType:ContractType,
                                     reference: String,
                                     name: String,
                                     format: Format,
                                     file: FileRepository,
                                     creationDate: Date,
                                     signedFile: Option[FileRepository],
                                     signatureDate: Option[Date]) {

  def buildContract(): Contract ={
    status match {
      case SIGNED => buildContractSigned()
      case UNSIGNED => buildContractUnSigned()
    }
  }

  private def buildContractSigned(): ContractSigned ={
    assert(status == SIGNED)
    assert(signatureDate.isDefined)
    assert(signedFile.isDefined)
    new ContractSigned(identity,contractType,reference,name,format,file,creationDate,signedFile.get,signatureDate.get)
  }

  private def buildContractUnSigned(): ContractUnSigned ={
    assert(status == UNSIGNED)
    new ContractUnSigned(identity,contractType,reference,name,format,file,creationDate)
  }

}

object ContractRepo {
  def Apply(contract: ContractSigned) : ContractRepo = {
    new ContractRepo(contract.identity,SIGNED,contract.contractType,contract.reference,contract.name,contract.format,contract.file,contract.creationDate,Some(contract.signedFile),Some(contract.signatureDate))
  }

  def Apply(contract: ContractUnSigned) : ContractRepo = {
    new ContractRepo(contract.identity,UNSIGNED,contract.contractType,contract.reference,contract.name,contract.format,contract.file,contract.creationDate,None,None)
  }
}

protected sealed trait ContractStatus
case object SIGNED extends ContractStatus
case object UNSIGNED extends ContractStatus