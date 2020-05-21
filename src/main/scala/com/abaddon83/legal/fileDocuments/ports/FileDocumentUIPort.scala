package com.abaddon83.legal.fileDocuments.ports

import com.abaddon83.legal.fileDocuments.domainModels.FileDocument
import com.abaddon83.legal.fileDocuments.services.FileDocumentService
import com.abaddon83.legal.sharedValueObjects.contracts.Format

import scala.concurrent.Future

trait FileDocumentUIPort {

  protected val fileDocumentService: FileDocumentService

  def createFileDocument(documentTemplateName: String, documentDetails: Map[String,String],format: Format): Future[FileDocument]
}
