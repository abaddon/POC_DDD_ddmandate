package com.abaddon83.libs.ddd

import java.util.UUID

trait AggregateUUIDIdentity extends AggregateIdentity {
  override type IdType = UUID

  override def toString(): String = s"AggregateUUIDIdentity-${id.toString}"
  override def convertTo(): UUID = id.asInstanceOf[UUID]
}
