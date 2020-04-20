package com.abaddon83.legal.domainModel.contract.Repositories

class XyzRepository(fileName: String) extends Repository{
  override val provider: String = "xyz"
  override def url: String = "https://repository.xyz/"++fileName
}
