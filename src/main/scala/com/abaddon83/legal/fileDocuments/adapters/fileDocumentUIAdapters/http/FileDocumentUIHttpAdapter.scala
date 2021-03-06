package com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapters.http


import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.util.ByteString
import com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapters.http.messages.ErrorFileDocument
import com.abaddon83.legal.fileDocuments.domainModels.FileDocument
import com.abaddon83.legal.fileDocuments.ports.{FileDocumentRepositoryPort, FileDocumentUIPort, FileBodyPort, FileDocumentTemplateRepositoryPort}
import com.abaddon83.legal.fileDocuments.services.FileDocumentService
import com.abaddon83.legal.shares.contracts.Format
import com.abaddon83.legal.shares.fileDocuments.FileDocumentIdentity
import com.abaddon83.libs.akkaHttp.routes.RouteRejectionHandler

import scala.concurrent.Future
import scala.util.{Failure, Success}

class FileDocumentUIHttpAdapter(implicit pdfMakerPort: FileBodyPort,
                                templateRepository: FileDocumentTemplateRepositoryPort,
                                fileDocumentRepository: FileDocumentRepositoryPort
                                ) extends FileDocumentUIPort with RouteRejectionHandler{

  override protected val fileDocumentService: FileDocumentService = new FileDocumentService(pdfMakerPort,templateRepository,fileDocumentRepository)

  override def createFileDocument(documentTemplateName: String, documentDetails: Map[String, String], format: Format): Future[FileDocument] = ???

  override def getFileDocument(fileDocumentIdentity: FileDocumentIdentity): Future[FileDocument] = {
    fileDocumentService.giveMeFileDocument(fileDocumentIdentity)
  }

  val routes: Route = {
    extractUri { uri =>
      pathPrefix("files") {
        handleRejections(globalRejectionHandler) {
          concat(
            pathPrefix(Segment) { fileName =>
              pathEndOrSingleSlash {
                get {
                  onComplete(getFileDocument(FileDocumentIdentity.apply(fileName))){
                    _ match {
                      case Success(fileDocument) => {
                        val entity = HttpEntity.Strict(MediaTypes.`application/octet-stream`, ByteString.apply(fileDocument.fileBinaries))
                        val httpResponse = HttpResponse(entity = entity)
                        complete(httpResponse)
                      }
                      case Failure(throwable) => throwable match {
                        case ex: NoSuchElementException => complete(StatusCodes.NotFound, ErrorFileDocument.build(ex, uri.path.toString()))
                        case ex: Exception => complete(StatusCodes.InternalServerError, ErrorFileDocument.build(ex, uri.path.toString()))
                      }
                    }
                  }

                }
              }
            }
          )
        }
      }
    }
  }
}
