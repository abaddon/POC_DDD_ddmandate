package com.abaddon83.legal.fileDocuments.services

import com.abaddon83.legal.fileDocuments.domainModels.{FileDocument, PDFFileDocument}
import com.abaddon83.legal.fileDocuments.ports.{FileDocumentRepositoryPort, PDFBuilderPort, TemplateRepositoryPort}
import com.abaddon83.legal.sharedValueObjects.fileDocuments.FileDocumentIdentity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FileDocumentService(pdfMakerPort: PDFBuilderPort,
                          templateRepository: TemplateRepositoryPort,
                          fileDocumentRepository: FileDocumentRepositoryPort
                         ) {

  def createNewPDFFileDocument(templateName: String, templateData:Map[String, String]): Future[FileDocument] = {
    for{
      documentTemplate <- templateRepository.findTemplateByName(templateName)
      fileBinaries <- pdfMakerPort.createFile(documentTemplate,templateData)
    } yield fileDocumentRepository.save(PDFFileDocument(fileBinaries))
  }

  def giveMeFileDocument(fileDocumentId: FileDocumentIdentity): Future[FileDocument] = {
    fileDocumentRepository.findByFileId(fileDocumentId)
  }
}
