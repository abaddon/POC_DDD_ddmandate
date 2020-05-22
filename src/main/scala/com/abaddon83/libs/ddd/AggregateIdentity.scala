package com.abaddon83.libs.ddd

trait AggregateIdentity {
  protected type IdType
  protected val id: IdType
  def convertTo(): IdType
}
