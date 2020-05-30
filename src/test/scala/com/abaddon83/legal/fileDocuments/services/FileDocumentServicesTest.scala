package com.abaddon83.legal.fileDocuments.services


import com.abaddon83.legal.fileDocuments.adapters.documentTemplateRepositoryAdapters.local.FileDocumentTemplateRepositoryLocalAdapter
import com.abaddon83.legal.fileDocuments.adapters.fileBodyAdapters.pdfBox.FileBodyPdfBoxAdapter
import com.abaddon83.legal.fileDocuments.adapters.fileDocumentRepositoryAdapters.local.FileDocumentRepositoryLocalAdapter
import com.abaddon83.legal.fileDocuments.ports.{FileBodyPort, FileDocumentRepositoryPort, FileDocumentTemplateRepositoryPort}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.immutable.HashMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import org.scalatest.RecoverMethods._


class FileDocumentServicesTest extends AnyFunSuite with ScalaFutures  {

  val pdfMakerPort: FileBodyPort = new FileBodyPdfBoxAdapter()
  val templateRepository: FileDocumentTemplateRepositoryPort = new FileDocumentTemplateRepositoryLocalAdapter()
  val fileDocumentRepository: FileDocumentRepositoryPort = new FileDocumentRepositoryLocalAdapter()
  val fileDocumentService: FileDocumentService = new FileDocumentService(pdfMakerPort,templateRepository,fileDocumentRepository)

  test("create a new file Document"){
    val documentData: Map[String,String] = HashMap("name3[first]"->"Myname",  "name3[last]"->"MyLastName")
    val templateName = "ddMandate"

    fileDocumentService.createNewPDFFileDocument(templateName,documentData).onComplete {
      case Success(fileDocumentCreated) => {
        val fileDocumentLoaded = fileDocumentService.giveMeFileDocument(fileDocumentCreated.identity).futureValue
        //assert(fileDocumentCreated.fileBinaries == fileDocumentLoaded.fileBinaries)
        assert(fileDocumentCreated.identity == fileDocumentLoaded.identity)
        assert(fileDocumentCreated.format == fileDocumentLoaded.format)
      }
      case Failure(ex) => assert(false, ex.getCause)
    }
  }

  //TO FIX
  test("create a new file Document with missing parameters"){
    val documentData: Map[String,String] = HashMap("name3[first]"->"Myname")
    val templateName = "ddMandate"

    recoverToSucceededIf[IllegalStateException] {
      fileDocumentService.createNewPDFFileDocument(templateName,documentData).failed
    }

    /* fileDocumentService.createNewPDFFileDocument(templateName,documentData).onComplete {
      case Success(fileDocumentCreated) => {
        val fileDocumentLoaded = fileDocumentService.giveMeFileDocument(fileDocumentCreated.identity).futureValue
        assert(fileDocumentCreated.fileBinaries == fileDocumentLoaded.fileBinaries)
        assert(fileDocumentCreated.identity == fileDocumentLoaded.identity)
        assert(fileDocumentCreated.format == fileDocumentLoaded.format)
      }
      case Failure(ex) => assert(false, ex.getCause)
    }*/

  }

}
