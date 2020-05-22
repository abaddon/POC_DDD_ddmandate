package com.abaddon83.legal.sharedValueObjects.bankAccounts

import java.util.UUID

import com.abaddon83.libs.ddd.AggregateUUIDIdentity

case class BankAccountIdentity (private val value:UUID) extends AggregateUUIDIdentity{
  override protected val id: UUID = value
  override def toString: String = "BankAccount-"+id.toString
}

object BankAccountIdentity{
  def randomIdentity(): BankAccountIdentity =  new BankAccountIdentity(UUID.randomUUID())
  def fromUUID(uuid:UUID): BankAccountIdentity =  new BankAccountIdentity(uuid)
}
