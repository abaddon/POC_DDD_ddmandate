package com.abaddon83.ddd

trait AggregateId {
  type IdType
  val value: IdType
}
