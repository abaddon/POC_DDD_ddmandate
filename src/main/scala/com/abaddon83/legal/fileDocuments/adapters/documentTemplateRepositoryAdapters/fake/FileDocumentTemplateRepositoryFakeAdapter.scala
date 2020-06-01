package com.abaddon83.legal.fileDocuments.adapters.documentTemplateRepositoryAdapters.fake

import com.abaddon83.legal.fileDocuments.domainModels.FileDocumentTemplate
import com.abaddon83.legal.fileDocuments.ports.FileDocumentTemplateRepositoryPort
import com.abaddon83.legal.fileDocuments.services.TemplateConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FileDocumentTemplateRepositoryFakeAdapter extends FileDocumentTemplateRepositoryPort{

  override def loadTemplate(templateConfig: TemplateConfig, filledData:Map[String, String]): Future[FileDocumentTemplate] = {
    Future{
        FileDocumentTemplate(templateConfig,filledData,"test".getBytes)
    }
  }
}
