package com.abaddon83.legal.fileDocuments.ports

import com.abaddon83.legal.fileDocuments.domainModels.FileDocument
import com.abaddon83.legal.sharedValueObjects.fileDocuments.FileDocumentIdentity

import scala.concurrent.Future

trait FileDocumentRepositoryPort {

  def save(fileDocument: FileDocument): FileDocument
  def findByFileId(fileDocumentIdentity: FileDocumentIdentity): Future[FileDocument]

}
