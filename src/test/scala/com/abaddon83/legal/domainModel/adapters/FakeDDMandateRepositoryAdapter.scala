package com.abaddon83.legal.domainModel.adapters

import java.util.Date

import com.abaddon83.legal.domainModel.contract.Contract
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.BankAccountIdentity
import com.abaddon83.legal.domainModel.ddMandates.{Creditor, DDMandate, DDMandateAccepted, DDMandateCanceled, DDMandateIdentity, DDMandateNotAccepted, DDMandateType, Debtor}
import com.abaddon83.legal.ports.DDMandateRepositoryPort

import scala.collection.mutable.ListBuffer

class FakeDDMandateRepositoryAdapter extends DDMandateRepositoryPort{

  override def findDDMandateNotAcceptedById(id: DDMandateIdentity): Option[DDMandateNotAccepted] = {
    repository.db.find(ddMandate =>
      ddMandate.identity == id && ddMandate.status == NOACCEPTED
    ).map(ddDomainRepo => ddDomainRepo.buildDDMandate()).asInstanceOf[Option[DDMandateNotAccepted]]
  }

  override def findDDMandateAcceptedById(id: DDMandateIdentity): Option[DDMandateAccepted] ={
    repository.db.find(ddMandate =>
      ddMandate.identity == id && ddMandate.status == ACCEPTED
    ).map(ddDomainRepo => ddDomainRepo.buildDDMandate()).asInstanceOf[Option[DDMandateAccepted]]
  }

  override def findAllDDMandatesByBankAccount(bankAccountId: BankAccountIdentity): List[DDMandate] = {
    repository.db.collect{
      case ddMandate if ddMandate.debtor.bankAccount.identity == bankAccountId => ddMandate.buildDDMandate()
    }.toList
  }

  override def save(ddMandate: DDMandateNotAccepted): DDMandateNotAccepted = {
    saveDDMandate(DDMandateRepo.apply(ddMandate))
    ddMandate
  }
  override def save(ddMandate: DDMandateAccepted): DDMandateAccepted = {
    saveDDMandate(DDMandateRepo.apply(ddMandate))
    ddMandate
  }

  override def save(ddMandate: DDMandateCanceled): DDMandateCanceled = {
    saveDDMandate(DDMandateRepo.apply(ddMandate))
    ddMandate
  }

  private def saveDDMandate(ddMandate: DDMandateRepo): Unit = {

    repository.db.find(mandateRepo => mandateRepo.identity == ddMandate.identity) match {
      case Some(existingMandateRepo) => update(existingMandateRepo,ddMandate)
      case None =>persist(ddMandate)
    }
  }

  private object repository{
    var db: ListBuffer[DDMandateRepo]= ListBuffer()
  }

  private def persist(ddMandate: DDMandateRepo): Unit = {
    repository.db = repository.db.addOne(ddMandate)
    //debug()
  }

  private def update(oldMandate: DDMandateRepo,newMandate: DDMandateRepo): Unit = {
    repository.db = repository.db-=oldMandate
    persist(newMandate)
  }

  private def debug(): Unit ={
    println(s"DD Mandate Repository size: ${repository.db.size}")
    println("---start---")
    repository.db.foreach(mandate => {
      println(s"mandate id: ${mandate.identity.uuid.toString} STATUS: ${mandate.status}")
      println(s"-->bankAccount id: ${mandate.debtor.bankAccount.identity.uuid.toString}")
    })
    println("---end---")
  }

}

protected case class DDMandateRepo(
  identity: DDMandateIdentity,
  status: Status,
  ddMandateType: DDMandateType,
  debtor: Debtor,
  creditor: Creditor,
  creationDate: Date,
  contract: Contract,
  cancellationDate: Option[Date]){

  def buildDDMandate(): DDMandate = {
    status match {
      case NOACCEPTED => buildDDMandateNotAccepted()
      case ACCEPTED => buildDDMandateAccepted()
      case CANCELED =>buildDDMandateCanceled()
    }
  }

  private def buildDDMandateNotAccepted(): DDMandateNotAccepted = {
    assert(status==NOACCEPTED)
  new DDMandateNotAccepted(identity,ddMandateType,debtor,creditor,creationDate,contract)
  }

  private def buildDDMandateAccepted(): DDMandateAccepted = {
    assert(status==ACCEPTED)
    new DDMandateAccepted(identity,ddMandateType,debtor,creditor,creationDate,contract)
  }

  private def buildDDMandateCanceled(): DDMandateCanceled = {
    assert(status==CANCELED)
    assert(cancellationDate.isDefined)
    new DDMandateCanceled(identity,ddMandateType,debtor,creditor,creationDate,contract,cancellationDate.get)
  }
}

object DDMandateRepo{

  def apply(ddMandate: DDMandateNotAccepted): DDMandateRepo = {
    val mandateRepo = new DDMandateRepo(ddMandate.identity,NOACCEPTED,ddMandate.ddMandateType,ddMandate.debtor,ddMandate.creditor,ddMandate.creationDate, ddMandate.contract,None)

    mandateRepo
  }

  def apply(ddMandate: DDMandateAccepted): DDMandateRepo = {
    val mandateRepo = new DDMandateRepo(ddMandate.identity,ACCEPTED,ddMandate.ddMandateType,ddMandate.debtor,ddMandate.creditor,ddMandate.creationDate, ddMandate.contract,None)

    mandateRepo
  }

  def apply(ddMandate: DDMandateCanceled): DDMandateRepo = {
    val mandateRepo = new DDMandateRepo(ddMandate.identity,CANCELED,ddMandate.ddMandateType,ddMandate.debtor,ddMandate.creditor,ddMandate.creationDate, ddMandate.contract,Some(ddMandate.cancellationDate))

    mandateRepo
  }

}

sealed trait Status
case object NOACCEPTED extends Status
case object ACCEPTED extends Status
case object CANCELED extends Status
