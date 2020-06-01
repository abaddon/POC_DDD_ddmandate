package com.abaddon83.legal.fileDocuments.adapters.fileBodyAdapters.fake

import com.abaddon83.legal.fileDocuments.domainModels.{FileBody, FileDocumentTemplate}
import com.abaddon83.legal.fileDocuments.ports.FileBodyPort

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FileBodyFakeAdapter extends FileBodyPort{

  override def createFile(fileDocumentTemplate: FileDocumentTemplate): Future[FileBody] = {
    Future{
      FileBody("fakeFile".getBytes)
    }
  }
}
