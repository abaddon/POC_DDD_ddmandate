package com.abaddon83.legal.adapters.ddMandateAdapters.components

import java.util.{Date, UUID}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.abaddon83.legal.domainModel.ddMandates.{Creditor, DDMandateNotAccepted, Debtor}
import com.abaddon83.legal.adapters.ddMandateAdapters.utils.ExtraMarshalling
import spray.json.DefaultJsonProtocol



case class ViewDDMandate(id: UUID, creditor: RestCreditor, debtor: RestDebtor, ddMandateType: String, creationDate: Date)

case class RestCreditor(legalEntityCode: String, businessName: String, bankAccountId: UUID, debtCode: String)

case class RestDebtor(userId: Int, firstName: String, lastName: String, taxCode:String, birthDate: Date, bankAccountId: UUID)


object ViewDDMandateJsonSupport extends DefaultJsonProtocol with SprayJsonSupport with ExtraMarshalling{

  implicit val restCreditorFormat = jsonFormat4(RestCreditor.apply)
  implicit val restDebtorFormat = jsonFormat6(RestDebtor.apply)
  implicit val restDDMandateFormat = jsonFormat5(ViewDDMandate.apply)

}

object ViewDDMandate{
  def apply(ddmandate: DDMandateNotAccepted):ViewDDMandate = {
    new ViewDDMandate(ddmandate.identity.uuid,RestCreditor.apply(ddmandate.creditor),RestDebtor.apply(ddmandate.debtor),ddmandate.ddMandateType.toString,ddmandate.creationDate)
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
