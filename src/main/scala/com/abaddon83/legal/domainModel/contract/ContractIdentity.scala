package com.abaddon83.legal.domainModel.contract

import java.util.UUID

import com.abaddon83.shared.ddd.AggregateUUIDId

case class ContractIdentity private(uuid:UUID) extends AggregateUUIDId{
  override val value: UUID = uuid
  override def toString: String = "Contract-"+value.toString

}

object   ContractIdentity{
  def apply(): ContractIdentity = new ContractIdentity(UUID.randomUUID())
  def apply(uuid:UUID): ContractIdentity = new ContractIdentity(uuid)
}
