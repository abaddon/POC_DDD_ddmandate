package com.abaddon83.legal.shares.ddMandates

import java.util.UUID

import com.abaddon83.libs.ddd.AggregateUUIDIdentity

case class DDMandateIdentity (private val value:UUID) extends AggregateUUIDIdentity{
  override protected val id: UUID = value
  override def toString: String = "DDMandate-"+id.toString
}

object DDMandateIdentity{
  def apply(): DDMandateIdentity =  new DDMandateIdentity(UUID.randomUUID())
  def apply(uuid:UUID): DDMandateIdentity =  new DDMandateIdentity(uuid)
}
