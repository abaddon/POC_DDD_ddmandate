package com.abaddon83.legal.fileDocuments.adapters.documentTemplateRepositoryAdapters.local

import java.nio.file.{Files, Paths}

import com.abaddon83.legal.fileDocuments.domainModels.FileDocumentTemplate
import com.abaddon83.legal.fileDocuments.ports.FileDocumentTemplateRepositoryPort
import com.abaddon83.legal.fileDocuments.services.{FileDocumentConfigService, TemplateConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FileDocumentTemplateRepositoryLocalAdapter extends FileDocumentTemplateRepositoryPort{

  override def loadTemplate(templateConfig: TemplateConfig, filledData:Map[String, String]): Future[FileDocumentTemplate] = {
    Future{
        val fileByteArray = Files.readAllBytes(Paths.get(getTemplatePath(templateConfig.fileName)))
        FileDocumentTemplate(templateConfig,filledData,fileByteArray)
    }
  }

  private def getTemplatePath(fileName: String): String ={
    s"${FileDocumentConfigService.getTemplatePath()}/$fileName"
  }
}
