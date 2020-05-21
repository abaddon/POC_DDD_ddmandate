package com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapter.akka.messages

import com.abaddon83.legal.fileDocuments.domainModels.{FileDocument, PDFFileDocument}

/*
val identity: FileDocumentIdentity
  val fileBinaries : Array[Byte]
  val format: Format
 */
case class FileDocumentMsg(id: String, fileBinaries : Array[Byte], format: String)

object FileDocumentMsg {
  def apply(fileDocument: FileDocument): FileDocumentMsg = {

    fileDocument match {
      case fileDoc: PDFFileDocument => convertTo(fileDoc)
      case fileDoc => throw new NotImplementedError()(s"File document ${fileDoc.getClass} not implemented")
    }
  }

  def convertTo(pdfFileDocument: PDFFileDocument): FileDocumentMsg = {
    FileDocumentMsg(pdfFileDocument.identity.id, pdfFileDocument.fileBinaries, "PDF")
  }

}


