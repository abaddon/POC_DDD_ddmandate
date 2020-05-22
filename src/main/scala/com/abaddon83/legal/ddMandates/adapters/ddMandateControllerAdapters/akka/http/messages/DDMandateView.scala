package com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.akka.http.messages

import java.util.{Date, UUID}

import com.abaddon83.legal.ddMandates.domainModels._


case class DDMandateView(id: UUID, status: String, creditor: RestCreditor, debtor: RestDebtor, contract: RestContract, ddMandateType: String, creationDate: Date)

case class RestCreditor(legalEntityCode: String, businessName: String, bankAccountId: UUID, debtCode: String)

case class RestDebtor(userId: Int, firstName: String, lastName: String, taxCode:String, birthDate: Date, bankAccountId: UUID)
case class RestContract(id: UUID, isSigned: Boolean)


object DDMandateView{
  def apply(ddmandate: DDMandate):DDMandateView = {
    ddmandate match {
      case mandate: DDMandateNotAccepted => convertTo(mandate)
      case mandate: DDMandateAccepted => convertTo(mandate)
      case mandate: DDMandateCanceled => convertTo(mandate)
      case _ => throw new IllegalArgumentException(s"Unrecognised status of the DD Mandate ${ddmandate.identity}")
    }
  }

  private def convertTo(ddmandate: DDMandateNotAccepted):DDMandateView = {
    new DDMandateView(ddmandate.identity.convertTo(),"Not Accepted",RestCreditor.apply(ddmandate.creditor),RestDebtor.apply(ddmandate.debtor),RestContract(ddmandate.contract),ddmandate.ddMandateType.toString,ddmandate.creationDate)
  }
  private def convertTo(ddmandate: DDMandateAccepted):DDMandateView = {
    new DDMandateView(ddmandate.identity.convertTo(),"Accepted",RestCreditor.apply(ddmandate.creditor),RestDebtor.apply(ddmandate.debtor),RestContract(ddmandate.contract),ddmandate.ddMandateType.toString,ddmandate.creationDate)
  }
  private def convertTo(ddmandate: DDMandateCanceled):DDMandateView = {
    new DDMandateView(ddmandate.identity.convertTo(),"Canceled",RestCreditor.apply(ddmandate.creditor),RestDebtor.apply(ddmandate.debtor),RestContract(ddmandate.contract),ddmandate.ddMandateType.toString,ddmandate.creationDate)
  }

}

object RestContract{
  def apply(contract: DDMandateContract): RestContract = {
    new RestContract(contract.identity.convertTo(),contract.isSigned)
  }
}

object RestCreditor{
  def apply(creditor: Creditor): RestCreditor = {
    new RestCreditor(creditor.legalEntityCode,creditor.businessName,creditor.bankAccount.identity.convertTo(), creditor.debtCode)
  }
}

object RestDebtor {
  def apply(debtor: Debtor): RestDebtor = {
    new RestDebtor(debtor.userId, debtor.firstName, debtor.lastName, debtor.taxCode, debtor.birthDate, debtor.bankAccount.identity.convertTo())
  }
}
