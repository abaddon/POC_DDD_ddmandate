package com.abaddon83.legal.ddMandates.adapters.ddMandateRepositoryAdapters.fake

import java.util.Date

import com.abaddon83.legal.ddMandates.domainModels.{Creditor, DDMandate, DDMandateAccepted, DDMandateCanceled, DDMandateContract, DDMandateNotAccepted, DDMandateType, Debtor}
import com.abaddon83.legal.ddMandates.ports.DDMandateRepositoryPort
import com.abaddon83.legal.sharedValueObjects.bankAccounts.BankAccountIdentity
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class FakeDDMandateRepositoryAdapter extends DDMandateRepositoryAdapter

object FakeDDMandateRepositoryAdapterSingleton extends DDMandateRepositoryAdapter

trait DDMandateRepositoryAdapter extends DDMandateRepositoryPort{

  override def findDDMandateNotAcceptedById(id: DDMandateIdentity): Future[DDMandateNotAccepted] = {
    findDDMandateById(id).flatMap {
      case ddmandate: DDMandateNotAccepted => Future(ddmandate)
      case _ => throw new NoSuchElementException(s"DDMandateNotAccepted with id: ${id.toString} not found")
    }
  }

  override def findDDMandateCancelledById(id: DDMandateIdentity): Future[DDMandateCanceled] = {
    findDDMandateById(id).flatMap {
      case ddmandate : DDMandateCanceled => Future(ddmandate)
      case _ => throw new NoSuchElementException(s"DDMandateCanceled with id: ${id.toString} not found")
    }
  }

  override def findDDMandateAcceptedById(id: DDMandateIdentity): Future[DDMandateAccepted] ={
    findDDMandateById(id).flatMap{
      case ddmandate : DDMandateAccepted => Future(ddmandate)
      case _ => throw new NoSuchElementException(s"DDMandateAccepted with id: ${id.toString} not found")
    }
  }

  override def findDDMandateById(id: DDMandateIdentity): Future[DDMandate] = {
    Future{
      repository.db.find(ddMandate =>
        ddMandate.identity == id
      ).map(ddDomainRepo => ddDomainRepo.buildDDMandate())
      .getOrElse(throw new NoSuchElementException(s"DDMandate with id: ${id.toString} not found"))
    }


  }

  override def findAllDDMandatesByBankAccount(bankAccountId: BankAccountIdentity): Future[List[DDMandate]] = {
    Future{
      repository.db.collect{
        case ddMandate if ddMandate.debtor.bankAccount.identity == bankAccountId => ddMandate.buildDDMandate()
      }.toList
    }
  }

  override def save(ddMandate: DDMandateNotAccepted): DDMandateNotAccepted = {
    saveDDMandate(DDMandateRepo(ddMandate))
    ddMandate
  }
  override def save(ddMandate: DDMandateAccepted): DDMandateAccepted = {
    saveDDMandate(DDMandateRepo(ddMandate))
    ddMandate
  }

  override def save(ddMandate: DDMandateCanceled): DDMandateCanceled = {
    saveDDMandate(DDMandateRepo(ddMandate))
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
  status: DDMandateStatus,
  ddMandateType: DDMandateType,
  debtor: Debtor,
  creditor: Creditor,
  creationDate: Date,
  contract: DDMandateContract,
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

protected sealed trait DDMandateStatus
case object NOACCEPTED extends DDMandateStatus
case object ACCEPTED extends DDMandateStatus
case object CANCELED extends DDMandateStatus
