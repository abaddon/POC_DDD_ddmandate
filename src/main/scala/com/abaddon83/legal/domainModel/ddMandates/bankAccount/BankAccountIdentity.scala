package com.abaddon83.legal.domainModel.ddMandates.bankAccount

import java.util.UUID

import com.abaddon83.shared.ddd.AggregateUUIDId

case class BankAccountIdentity(uuid: UUID) extends AggregateUUIDId{
  override val value: UUID = uuid
  override def toString: String ="BankAccount-"++value.toString
}

object BankAccountIdentity{
  def apply(): BankAccountIdentity =  new BankAccountIdentity(UUID.randomUUID())
  def apply(uuid:UUID): BankAccountIdentity =  new BankAccountIdentity(uuid)
}
