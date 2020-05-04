package com.abaddon83.legal.adapters.ddMandateAdapters.components

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.abaddon83.legal.adapters.ddMandateAdapters.utils.ExtraMarshalling
import spray.json.DefaultJsonProtocol

case class CreateDDMandate(
                                 bankAccountId: UUID,
                                 legalEntity: String
                                 )
object CreateDDMandateJsonSupport extends DefaultJsonProtocol with SprayJsonSupport with ExtraMarshalling{

  implicit val createDDMandateFormat = jsonFormat2(CreateDDMandate.apply)
}
