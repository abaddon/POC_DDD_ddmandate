package com.abaddon83.legal.ddMandates.adapters.ddMandateUIAdapters.akka.responses

import java.util.{Date, UUID}

import com.abaddon83.legal.ddMandates.domainModels._

sealed trait Reply
case class DDMandateMsg(id: UUID, status: String, creditor: RestCreditor, debtor: RestDebtor, ddMandateType: String, creationDate: Date) extends Reply

case class RestCreditor(legalEntityCode: String, businessName: String, bankAccountId: UUID, debtCode: String)

case class RestDebtor(userId: Int, firstName: String, lastName: String, taxCode:String, birthDate: Date, bankAccountId: UUID)


object DDMandateMsg {
  def apply(ddmandate: DDMandate):DDMandateMsg = {
    ddmandate match {
      case mandate: DDMandateNotAccepted => convertTo(mandate)
      case mandate: DDMandateAccepted => convertTo(mandate)
      case mandate: DDMandateCanceled => convertTo(mandate)
      case _ => throw new IllegalArgumentException(s"Unrecognised status of the DD Mandate ${ddmandate.identity.convertTo()}")
    }
  }

  private def convertTo(ddmandate: DDMandateNotAccepted):DDMandateMsg = {
    new DDMandateMsg(ddmandate.identity.convertTo(),"Not Accepted",RestCreditor.apply(ddmandate.creditor),RestDebtor.apply(ddmandate.debtor),ddmandate.ddMandateType.toString,ddmandate.creationDate)
  }
  private def convertTo(ddmandate: DDMandateAccepted):DDMandateMsg = {
    new DDMandateMsg(ddmandate.identity.convertTo(),"Accepted",RestCreditor.apply(ddmandate.creditor),RestDebtor.apply(ddmandate.debtor),ddmandate.ddMandateType.toString,ddmandate.creationDate)
  }
  private def convertTo(ddmandate: DDMandateCanceled):DDMandateMsg = {
    new DDMandateMsg(ddmandate.identity.convertTo(),"Canceled",RestCreditor.apply(ddmandate.creditor),RestDebtor.apply(ddmandate.debtor),ddmandate.ddMandateType.toString,ddmandate.creationDate)
  }

}

object RestCreditor {
  def apply(creditor: Creditor): RestCreditor = {
    new RestCreditor(creditor.legalEntityCode,creditor.businessName,creditor.bankAccount.identity.convertTo(), creditor.debtCode)
  }
}

object RestDebtor {
  def apply(debtor: Debtor): RestDebtor = {
    new RestDebtor(debtor.userId, debtor.firstName, debtor.lastName, debtor.taxCode, debtor.birthDate, debtor.bankAccount.identity.convertTo())
  }
}
