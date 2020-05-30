package com.abaddon83.legal.fileDocuments.ports

import com.abaddon83.legal.fileDocuments.domainModels.{FileBody, FileDocumentTemplate}

import scala.concurrent.Future

trait FileBodyPort {
  def createFile(template: FileDocumentTemplate): Future[FileBody]

}
