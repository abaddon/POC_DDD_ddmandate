package com.abaddon83.legal.sharedValueObjects.contracts

import java.util.UUID

import com.abaddon83.libs.ddd.AggregateUUIDId

case class ContractIdentity private(uuid:UUID) extends AggregateUUIDId{
  override val value: UUID = uuid
  override def toString: String = "Contract-"+value.toString

}

object ContractIdentity {
  def apply(): ContractIdentity = new ContractIdentity(UUID.randomUUID())
  def apply(uuid:UUID): ContractIdentity = new ContractIdentity(uuid)
}
