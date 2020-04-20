package com.abaddon83.legal.domainModel.contract.Repositories

trait  Repository {
  val provider: String
  def url: String
}
