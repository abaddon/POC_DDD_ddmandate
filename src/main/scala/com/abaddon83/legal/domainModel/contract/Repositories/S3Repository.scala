package com.abaddon83.legal.domainModel.contract.Repositories

case class S3Repository(key: String, container: String) extends Repository{
  override val provider: String = "S3"
  override def url: String = "https://"++container++"/"++key
}
