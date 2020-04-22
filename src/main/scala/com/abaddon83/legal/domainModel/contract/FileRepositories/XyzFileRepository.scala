package com.abaddon83.legal.domainModel.contract.FileRepositories

class XyzFileRepository(fileName: String) extends FileRepository{
  override val provider: String = "xyz"
  override def url: String = "https://repository.xyz/"++fileName
}
