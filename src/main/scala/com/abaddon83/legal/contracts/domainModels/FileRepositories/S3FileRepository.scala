package com.abaddon83.legal.contracts.domainModels.FileRepositories

case class S3FileRepository(key: String, container: String) extends FileRepository{
  override val provider: String = "S3"
  override def url: String = "https://"++container++"/"++key
}
