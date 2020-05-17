package com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.http.messages

import com.abaddon83.libs.akkaHttp.messages.GenericJsonSupport

trait ContractJsonSupport  extends GenericJsonSupport{

  implicit val CreateContractFormat = jsonFormat2(CreateContractRequest)
  implicit val fileRepositoryViewFormat = jsonFormat2(FileRepositoryView.apply)
  implicit val contractViewFormat = jsonFormat10(ContractView.apply)
  implicit val signContractRequestFormat = jsonFormat1(SignContractRequest.apply)

}
