package com.abaddon83.legal.fileDocuments.services


import com.abaddon83.legal.fileDocuments.adapters.fileBodyAdapters.pdfBox.FileBodyPdfBoxAdapter
import com.abaddon83.legal.fileDocuments.adapters.fileDocumentRepositoryAdapters.localFS.FileDocumentRepositoryLocalFSAdapter
import com.abaddon83.legal.fileDocuments.adapters.templateRepositoryAdapters.fake.TemplateRepositoryFakeAdapter
import com.abaddon83.legal.fileDocuments.domainModels.{DocumentTemplate, PDFFileDocument}
import com.abaddon83.legal.fileDocuments.ports.{FileBodyPort, FileDocumentRepositoryPort, TemplateRepositoryPort}
import com.abaddon83.legal.shares.contracts.Format
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.immutable.HashMap
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class FileDocumentServicesTest extends AnyFunSuite with ScalaFutures  {

  val pdfMakerPort: FileBodyPort = new FileBodyPdfBoxAdapter()
  val templateRepository: TemplateRepositoryPort = new TemplateRepositoryFakeAdapter()
  val fileDocumentRepository: FileDocumentRepositoryPort = new FileDocumentRepositoryLocalFSAdapter()
  val fileDocumentService: FileDocumentService = new FileDocumentService(pdfMakerPort,templateRepository,fileDocumentRepository)

  test("store a file"){
    val documentData: Map[String,String] = HashMap("name3[first]"->"Myname", "name3[last]"->"MyLastName")

    /*templateRepository.findTemplateByName("name") onComplete {
      case Success(value) => {
        println(s"value.name: ${value.name}")
        println(s"body: ${value.body.size}")
      }
      case Failure(t) => println("An error has occurred: " + t.getMessage)
    }

    val template = templateRepository.findTemplateByName("name").futureValue

    pdfMakerPort.createFile(template,documentData) onComplete {
      case Success(value) => {
        println(s"body2: ${value.size}")
        pdfMakerPort.createFile(template,documentData) onComplete {
          case Success(value) => {
            println(s"pdfMakerPort.createFile: ${value}")
            val pdfFileDocument=PDFFileDocument.create(value)
            println(s"pdfFileDocument.identify: ${pdfFileDocument.identify}")
            println(s"pdfFileDocument.format: ${pdfFileDocument.format}")
            println(s"pdfFileDocument.fileBinaries: ${pdfFileDocument.fileBinaries}")
            println(s"pdfFileDocument.getLocalPath(): ${pdfFileDocument.getLocalPath()}")
            fileDocumentRepository.save(pdfFileDocument) onComplete {
              case Success(value) => println(s"pdfFileDocument.identify: ${value.identify}")
              case Failure(t) => println("An error has occurred: " + t.getMessage)
            }
          }
          case Failure(t) => println("An error has occurred: " + t.getMessage)
        }
      }
      case Failure(t) => println("An error has occurred: " + t.getMessage)
    }
    //val fileBody =
*/

    fileDocumentService.createNewPDFFileDocument("name",documentData) onComplete {
      case Success(value) => {
        assert(value.format == Format.PDF)
      }
      case Failure(t) => assert(false,s"An error has occurred: ${t.getMessage}")
    }

  }

}
