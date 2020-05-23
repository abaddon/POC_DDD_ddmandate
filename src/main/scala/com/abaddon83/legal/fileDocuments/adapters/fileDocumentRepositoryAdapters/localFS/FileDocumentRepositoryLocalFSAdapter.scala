package com.abaddon83.legal.fileDocuments.adapters.fileDocumentRepositoryAdapters.localFS

import java.io.{File, FileInputStream, FileOutputStream}

import com.abaddon83.legal.fileDocuments.domainModels.{FileDocument, PDFFileDocument}
import com.abaddon83.legal.fileDocuments.ports.FileDocumentRepositoryPort
import com.abaddon83.legal.shares.contracts.Format
import com.abaddon83.legal.shares.fileDocuments.FileDocumentIdentity

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class FileDocumentRepositoryLocalFSAdapter extends FileDocumentRepositoryPort{

  var path: String = "./fileRepository"

  override def save(fileDocument: FileDocument): FileDocument = {
    persist(fileDocument)
  }

  override def findByFileId(fileDocumentIdentity: FileDocumentIdentity): Future[FileDocument] ={
    Future{
      load(fileDocumentIdentity)
    }
  }

  private def persist(fileDocument: FileDocument) : FileDocument={
    println(s"->>>> getFullPath(fileDocument.identity): ${getFullPath(fileDocument.identity)}")
    val file: File = new File(getFullPath(fileDocument.identity))
    var out = new FileOutputStream(file)
    out.write(fileDocument.fileBinaries)
    out.close
    fileDocument
  }

  private def load(fileDocumentIdentity: FileDocumentIdentity): FileDocument ={
    val file: File = new File(getFullPath(fileDocumentIdentity))
    var in = new FileInputStream(file)

    val fileBody=LazyList.continually(in.read).takeWhile(_ != -1).map(_.toByte).toArray
    fileDocumentIdentity.format match {
      case Format.PDF => new PDFFileDocument(fileDocumentIdentity,fileBody,fileDocumentIdentity.format)
      case Format.DOC => throw new NotImplementedError("DOC format not implemented")
      case Format.JPG => throw new NotImplementedError("JPG format not implemented")
    }
  }

  private def getFullPath(fileDocumentIdentity: FileDocumentIdentity): String = {
    s"${path}/${fileDocumentIdentity.convertTo()}"
  }
}
