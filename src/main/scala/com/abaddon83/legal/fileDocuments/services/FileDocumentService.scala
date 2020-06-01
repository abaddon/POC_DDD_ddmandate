package com.abaddon83.legal.fileDocuments.services

import com.abaddon83.legal.fileDocuments.domainModels.{FileDocument, PDFFileDocument}
import com.abaddon83.legal.fileDocuments.ports.{FileBodyPort, FileDocumentRepositoryPort, FileDocumentTemplateRepositoryPort}
import com.abaddon83.legal.shares.fileDocuments.FileDocumentIdentity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FileDocumentService(fileBodyPort: FileBodyPort,
                          documentTemplateRepository: FileDocumentTemplateRepositoryPort,
                          fileDocumentRepository: FileDocumentRepositoryPort
                         ) {

  def createNewPDFFileDocument(templateName: String, templateData:Map[String, String]): Future[FileDocument] = {
    //try{
      val templateConfig = FileDocumentConfigService.getTemplateConfig(templateName)
      for{
        documentTemplate <- documentTemplateRepository.loadTemplate(templateConfig,templateData)
        fileBinaries <- fileBodyPort.createFile(documentTemplate)
      } yield fileDocumentRepository.save(PDFFileDocument(fileBinaries))
    //} catch {
    //  case e: NoSuchElementException => throw e
    //  case e: Exception => throw e
    //}
  }

  def giveMeFileDocument(fileDocumentId: FileDocumentIdentity): Future[FileDocument] = {
    fileDocumentRepository.findByFileId(fileDocumentId)
  }
}
