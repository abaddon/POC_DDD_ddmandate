package com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapters.akka.messages

import com.abaddon83.legal.fileDocuments.domainModels.{FileDocument, PDFFileDocument}
import sun.reflect.generics.reflectiveObjects.NotImplementedException

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
      case fileDoc => throw new NotImplementedException()
    }
  }

  def convertTo(pdfFileDocument: PDFFileDocument): FileDocumentMsg = {
    FileDocumentMsg(pdfFileDocument.identity.convertTo(), pdfFileDocument.fileBinaries, "PDF")
  }

}


