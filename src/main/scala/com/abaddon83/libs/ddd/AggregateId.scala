package com.abaddon83.libs.ddd

trait AggregateId {
  type IdType
  val value: IdType
}
