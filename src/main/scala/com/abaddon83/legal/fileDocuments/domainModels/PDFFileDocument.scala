package com.abaddon83.legal.fileDocuments.domainModels

import java.util.UUID

import com.abaddon83.legal.shares.contracts.Format
import com.abaddon83.legal.shares.fileDocuments.FileDocumentIdentity


case class PDFFileDocument( identity: FileDocumentIdentity,
                            fileBinaries : Array[Byte],
                            format: Format
                          ) extends FileDocument{

}

object PDFFileDocument {
  def apply(fileBody : FileBody): PDFFileDocument = {
    new PDFFileDocument(FileDocumentIdentity(UUID.randomUUID(),Format.PDF),fileBody.body,Format.PDF)
  }
}