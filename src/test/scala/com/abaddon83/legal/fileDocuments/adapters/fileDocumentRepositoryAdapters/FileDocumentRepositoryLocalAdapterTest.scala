package com.abaddon83.legal.fileDocuments.adapters.fileDocumentRepositoryAdapters

import java.util.UUID

import com.abaddon83.legal.fileDocuments.adapters.fileDocumentRepositoryAdapters.local.FileDocumentRepositoryLocalAdapter
import com.abaddon83.legal.fileDocuments.domainModels.PDFFileDocument
import com.abaddon83.legal.shares.contracts.Format.PDF
import com.abaddon83.legal.shares.fileDocuments.FileDocumentIdentity
import com.abaddon83.legal.utilities.UUIDRegistryHelper
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite

class FileDocumentRepositoryLocalAdapterTest extends AnyFunSuite with ScalaFutures{

  val fileDocumentRepositoryLocalAdapter = new FileDocumentRepositoryLocalAdapter()

  test("persist a file"){

    val uuid = UUID.randomUUID()
    val fileDocumentIdentity = FileDocumentIdentity(uuid, PDF)
    val binary = "dddd".getBytes
    val fileDocument = new PDFFileDocument(fileDocumentIdentity,binary,fileDocumentIdentity.format)

    val fileDocumentSaved =fileDocumentRepositoryLocalAdapter.save(fileDocument)

    assert(fileDocumentSaved.identity == fileDocumentIdentity)
    assert(fileDocument.fileBinaries == binary)
    assert(fileDocument.format == PDF)

    UUIDRegistryHelper.add("fileDocument",uuid,"saved")
  }

  test("load a file"){
    val uuid =UUIDRegistryHelper.search("fileDocument","saved").get
    val fileDocumentIdentity = FileDocumentIdentity(uuid, PDF)
    val fileDocumentLoaded = fileDocumentRepositoryLocalAdapter.findByFileId(fileDocumentIdentity).futureValue
    assert(fileDocumentLoaded.identity == fileDocumentIdentity)
  }

}
