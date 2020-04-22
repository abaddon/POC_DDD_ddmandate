package com.abaddon83.legal.domainModel.adapters

import com.abaddon83.legal.domainModel.ddMandates.bankAccount.BankAccountIdentity
import com.abaddon83.legal.domainModel.ddMandates.{ACCEPTED, DDMandate, DDMandateAccepted, DDMandateCanceled, DDMandateDraft, DDMandateIdentity, DDMandateNotAccepted, Debtor, NOACCEPTED}
import com.abaddon83.legal.ports.DDMandateRepositoryPort

import scala.collection.mutable.ListBuffer

class FakeDDMandateRepositoryAdapter extends DDMandateRepositoryPort{

  override def findDDMandateNotAcceptedById(id: DDMandateIdentity): Option[DDMandateNotAccepted] = {
    repository.db.find(ddMandate =>
      ddMandate.identity == id && ddMandate.status == NOACCEPTED
    ).asInstanceOf[Option[DDMandateNotAccepted]]
  }

  override def findDDMandateAcceptedById(id: DDMandateIdentity): Option[DDMandateAccepted] ={
    repository.db.find(ddMandate =>
      ddMandate.identity == id && ddMandate.status == ACCEPTED
    ).asInstanceOf[Option[DDMandateAccepted]]
  }

  override def findAllDDMandatesByBankAccount(bankAccountId: BankAccountIdentity): List[DDMandate] = {
    repository.db.collect{
      case ddMandate if ddMandate.debtor.bankAccount.identity == bankAccountId => ddMandate
    }.toList
  }

  override def save(ddMandate: DDMandateNotAccepted): DDMandateNotAccepted = {
    saveDDMandate(ddMandate)
    ddMandate
  }
  override def save(ddMandate: DDMandateAccepted): DDMandateAccepted = {
    saveDDMandate(ddMandate)
    ddMandate
  }

  override def save(ddMandate: DDMandateCanceled): DDMandateCanceled = {
    saveDDMandate(ddMandate)
    ddMandate
  }

  private def saveDDMandate(ddMandate: DDMandate) = {

    repository.db.find(mandate => mandate.identity == ddMandate.identity) match {
      case Some(existingMandate) => update(existingMandate,ddMandate)
      case None =>persist(ddMandate)
    }
  }

  private object repository{
    var db: ListBuffer[DDMandate]= ListBuffer()
  }

  private def persist(ddMandate: DDMandate) = {
    repository.db = repository.db.addOne(ddMandate)
    println(s"DD Mandate Repository size: ${repository.db.size}")
    println("---start---")
    repository.db.foreach(mandate => {
      println(s"mandate id: ${mandate.identity.uuid.toString} STATUS: ${mandate.status}")
      println(s"-->bankAccount id: ${mandate.debtor.bankAccount.identity.uuid.toString}")
    })

    println("---end---")
  }

  private def update(oldMandate: DDMandate,newMandate: DDMandate) = {
    repository.db = repository.db-=oldMandate
    persist(newMandate)
  }


}
