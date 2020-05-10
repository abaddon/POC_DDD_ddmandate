package com.abaddon83.legal.adapters.ddMandateAdapters.AkkaHttp.messages

import com.abaddon83.shared.akkaHttp.messages.GenericJsonSupport

trait DDMandateJsonSupport extends GenericJsonSupport{

  implicit val restCreditorFormat = jsonFormat4(RestCreditor.apply)
  implicit val restDebtorFormat = jsonFormat6(RestDebtor.apply)
  implicit val restDDMandateFormat = jsonFormat6(RestViewDDMandate.apply)
  implicit val createDDMandateFormat = jsonFormat2(CreateDDMandate.apply)

}
