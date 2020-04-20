package com.abaddon83.legal.domainModel.ddMandates

import java.util.UUID

import com.abaddon83.ddd.AggregateUUIDId

case class DDMandateIdentity private(uuid:UUID) extends AggregateUUIDId{
  override val value : UUID = uuid
}

object DDMandateIdentity{
  def apply(): DDMandateIdentity =  new DDMandateIdentity(UUID.randomUUID())
  def apply(uuid:UUID): DDMandateIdentity =  new DDMandateIdentity(uuid)
}
