package com.abaddon83.legal.fileDocuments.adapters.fileDocumentRepositoryAdapters.fake

import com.abaddon83.legal.fileDocuments.domainModels.FileDocument
import com.abaddon83.legal.fileDocuments.ports.FileDocumentRepositoryPort
import com.abaddon83.legal.fileDocuments.services.FileDocumentConfigService
import com.abaddon83.legal.shares.fileDocuments.FileDocumentIdentity

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FileDocumentRepositoryFakeAdapter extends FileDocumentRepositoryPort{

  var path: String = FileDocumentConfigService.getFileRepositoryPath()

  override def save(fileDocument: FileDocument): FileDocument = {
    fileDocumentRepository.addOne(fileDocument)
    fileDocument
  }

  override def findByFileId(fileDocumentIdentity: FileDocumentIdentity): Future[FileDocument] ={
    Future{
      fileDocumentRepository.find(fileDoc => fileDoc.identity == fileDocumentIdentity).getOrElse(throw new NoSuchElementException(s"FileDocument with id: ${fileDocumentIdentity} not found"))
    }
  }

  private val fileDocumentRepository: ListBuffer[FileDocument] = new ListBuffer[FileDocument]


}
