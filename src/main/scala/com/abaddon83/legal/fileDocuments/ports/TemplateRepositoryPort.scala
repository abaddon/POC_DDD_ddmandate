package com.abaddon83.legal.fileDocuments.ports

import com.abaddon83.legal.fileDocuments.domainModels.DocumentTemplate

import scala.concurrent.Future

trait TemplateRepositoryPort {

  def findTemplateByName(name:String): Future[DocumentTemplate]
}
