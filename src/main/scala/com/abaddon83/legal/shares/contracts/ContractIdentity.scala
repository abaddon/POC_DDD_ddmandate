package com.abaddon83.legal.shares.contracts

import java.util.UUID

import com.abaddon83.libs.ddd.AggregateUUIDIdentity

case class ContractIdentity (private val value:UUID) extends AggregateUUIDIdentity{
  override protected val id: UUID = value
  override def toString: String = "Contract-"+id.toString
}

object ContractIdentity {
  def apply(): ContractIdentity = new ContractIdentity(UUID.randomUUID())
  def apply(uuid:UUID): ContractIdentity = new ContractIdentity(uuid)
}
