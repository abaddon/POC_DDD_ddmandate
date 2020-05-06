package com.abaddon83.shared.ddd

trait AggregateId {
  type IdType
  val value: IdType
}
