package com.abaddon83.legal.ddMandates.adapters.ddMandateUIAdapters.akkaHttp.messages

import com.abaddon83.libs.akkaHttp.messages.GenericJsonSupport

trait DDMandateJsonSupport extends GenericJsonSupport{

  implicit val restContractFormat = jsonFormat2(RestContract.apply)
  implicit val restCreditorFormat = jsonFormat4(RestCreditor.apply)
  implicit val restDebtorFormat = jsonFormat6(RestDebtor.apply)
  implicit val restDDMandateFormat = jsonFormat7(DDMandateView.apply)
  implicit val createDDMandateFormat = jsonFormat2(CreateDDMandateRequest.apply)


}
