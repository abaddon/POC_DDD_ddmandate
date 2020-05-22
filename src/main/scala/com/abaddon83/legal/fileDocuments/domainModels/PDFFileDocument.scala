package com.abaddon83.legal.fileDocuments.domainModels

import java.util.UUID

import com.abaddon83.legal.sharedValueObjects.contracts.Format
import com.abaddon83.legal.sharedValueObjects.fileDocuments.FileDocumentIdentity


case class PDFFileDocument( identity: FileDocumentIdentity,
                            fileBinaries : Array[Byte],
                            format: Format
                          ) extends FileDocument{

  val path: String = "."

  def getLocalPath(): String = {
    s"${path}/${identity.convertTo()}"
  }
}

object PDFFileDocument {
  def apply(file : Array[Byte]): PDFFileDocument = {
    new PDFFileDocument(FileDocumentIdentity(UUID.randomUUID(),Format.PDF),file,Format.PDF)
  }
}