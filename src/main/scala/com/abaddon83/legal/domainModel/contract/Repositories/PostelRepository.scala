package com.abaddon83.legal.domainModel.contract.Repositories

class PostelRepository (fileName: String) extends Repository{
  override val provider: String = "Postel"
  override def url: String = "https://postel.poste.it/"++fileName
}
