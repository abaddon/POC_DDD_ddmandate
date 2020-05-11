package com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp.messages

import java.util.{Date, UUID}

import com.abaddon83.legal.ddMandates.domainModels.{Creditor, DDMandate, DDMandateAccepted, DDMandateCanceled, DDMandateNotAccepted, Debtor}


case class RestViewDDMandate(id: UUID, status: String, creditor: RestCreditor, debtor: RestDebtor, ddMandateType: String, creationDate: Date)

case class RestCreditor(legalEntityCode: String, businessName: String, bankAccountId: UUID, debtCode: String)

case class RestDebtor(userId: Int, firstName: String, lastName: String, taxCode:String, birthDate: Date, bankAccountId: UUID)


object RestViewDDMandate{
  def apply(ddmandate: DDMandate):RestViewDDMandate = {
    ddmandate match {
      case mandate: DDMandateNotAccepted => convertTo(mandate)
      case mandate: DDMandateAccepted => convertTo(mandate)
      case mandate: DDMandateCanceled => convertTo(mandate)
      case _ => throw new IllegalArgumentException(s"Unrecognised status of the DD Mandate ${ddmandate.identity.uuid}")
    }
  }

  private def convertTo(ddmandate: DDMandateNotAccepted):RestViewDDMandate = {
    new RestViewDDMandate(ddmandate.identity.uuid,"Not Accepted",RestCreditor.apply(ddmandate.creditor),RestDebtor.apply(ddmandate.debtor),ddmandate.ddMandateType.toString,ddmandate.creationDate)
  }
  private def convertTo(ddmandate: DDMandateAccepted):RestViewDDMandate = {
    new RestViewDDMandate(ddmandate.identity.uuid,"Accepted",RestCreditor.apply(ddmandate.creditor),RestDebtor.apply(ddmandate.debtor),ddmandate.ddMandateType.toString,ddmandate.creationDate)
  }
  private def convertTo(ddmandate: DDMandateCanceled):RestViewDDMandate = {
    new RestViewDDMandate(ddmandate.identity.uuid,"Canceled",RestCreditor.apply(ddmandate.creditor),RestDebtor.apply(ddmandate.debtor),ddmandate.ddMandateType.toString,ddmandate.creationDate)
  }

}

object RestCreditor{
  def apply(creditor: Creditor): RestCreditor = {
    new RestCreditor(creditor.legalEntityCode,creditor.businessName,creditor.bankAccount.identity.uuid, creditor.debtCode)
  }
}

object RestDebtor {
  def apply(debtor: Debtor): RestDebtor = {
    new RestDebtor(debtor.userId, debtor.firstName, debtor.lastName, debtor.taxCode, debtor.birthDate, debtor.bankAccount.identity.uuid)
  }
}
