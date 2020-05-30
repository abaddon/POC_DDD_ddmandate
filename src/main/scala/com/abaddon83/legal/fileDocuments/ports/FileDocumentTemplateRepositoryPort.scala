package com.abaddon83.legal.fileDocuments.ports

import com.abaddon83.legal.fileDocuments.domainModels.{FileDocumentTemplate}
import com.abaddon83.legal.fileDocuments.services.TemplateConfig

import scala.concurrent.Future

trait FileDocumentTemplateRepositoryPort {

  def loadTemplate(templateConfig:TemplateConfig, filledData:Map[String, String]): Future[FileDocumentTemplate]
}
