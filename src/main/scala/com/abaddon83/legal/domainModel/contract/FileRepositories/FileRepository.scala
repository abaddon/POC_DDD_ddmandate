package com.abaddon83.legal.domainModel.contract.FileRepositories

trait  FileRepository {
  val provider: String
  def url: String
}
