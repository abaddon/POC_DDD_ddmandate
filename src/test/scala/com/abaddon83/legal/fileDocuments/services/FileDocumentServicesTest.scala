package com.abaddon83.legal.fileDocuments.services


import com.abaddon83.legal.fileDocuments.adapters.fileDocumentRepositoryAdapters.local.LocalFileDocumentRepositoryAdapter
import com.abaddon83.legal.fileDocuments.adapters.pdfBuilderAdapters.pdfbox.PdfBoxPdfBuilderAdapter
import com.abaddon83.legal.fileDocuments.adapters.templateRepositoryAdapters.fake.FakeTemplateRepository
import com.abaddon83.legal.fileDocuments.domainModels.{DocumentTemplate, PDFFileDocument}
import com.abaddon83.legal.fileDocuments.ports.{FileDocumentRepositoryPort, PDFBuilderPort, TemplateRepositoryPort}
import com.abaddon83.legal.sharedValueObjects.contracts.Format
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.immutable.HashMap
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class FileDocumentServicesTest extends AnyFunSuite with ScalaFutures  {

  val pdfMakerPort: PDFBuilderPort = new PdfBoxPdfBuilderAdapter()
  val templateRepository: TemplateRepositoryPort = new FakeTemplateRepository()
  val fileDocumentRepository: FileDocumentRepositoryPort = new LocalFileDocumentRepositoryAdapter()
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
