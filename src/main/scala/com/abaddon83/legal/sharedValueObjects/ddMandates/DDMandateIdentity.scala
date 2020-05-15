package com.abaddon83.legal.sharedValueObjects.ddMandates

import java.util.UUID

import com.abaddon83.libs.ddd.AggregateUUIDId

case class DDMandateIdentity private(uuid:UUID) extends AggregateUUIDId{
  override val value : UUID = uuid
  override def toString: String = "DDMandate-"+value.toString
}

object DDMandateIdentity{
  def apply(): DDMandateIdentity =  new DDMandateIdentity(UUID.randomUUID())
  def apply(uuid:UUID): DDMandateIdentity =  new DDMandateIdentity(uuid)
}