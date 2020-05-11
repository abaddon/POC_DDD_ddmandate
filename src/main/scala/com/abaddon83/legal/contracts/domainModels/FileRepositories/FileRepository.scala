package com.abaddon83.legal.contracts.domainModels.FileRepositories

trait  FileRepository {
  val provider: String
  def url: String
}
