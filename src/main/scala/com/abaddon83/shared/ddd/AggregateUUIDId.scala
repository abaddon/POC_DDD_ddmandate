package com.abaddon83.shared.ddd

import java.util.UUID

trait AggregateUUIDId extends AggregateId {
  override type IdType = UUID
}
