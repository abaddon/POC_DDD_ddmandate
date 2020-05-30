package com.abaddon83.legal.fileDocuments.domainModels

import com.abaddon83.legal.shares.contracts.Format.PDF
import org.scalatest.funsuite.AnyFunSuite

class PDFFileDocumentTest extends AnyFunSuite {

  test("create new PDFFileDocument") {

    val fileBody = FileBody("test".getBytes)
    val pdfFileDocument = PDFFileDocument(fileBody)

    assert(pdfFileDocument.format == PDF)
    assert(pdfFileDocument.fileBinaries == fileBody.body)
  }

}
