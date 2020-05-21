package com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapter.akka

import akka.actor.{Actor, ActorLogging}
import akka.pattern._
import com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapter.akka.commands.{CreateFileDocumentCmd, GiveMeFileDocumentCmd}
import com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapter.akka.messages.FileDocumentMsg
import com.abaddon83.legal.fileDocuments.domainModels.FileDocument
import com.abaddon83.legal.fileDocuments.ports.{FileDocumentRepositoryPort, FileDocumentUIPort, PDFBuilderPort, TemplateRepositoryPort}
import com.abaddon83.legal.fileDocuments.services.FileDocumentService
import com.abaddon83.legal.sharedValueObjects.contracts.Format

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AkkaFileDocumentUIAdapter()(implicit pdfMakerPort: PDFBuilderPort,
                                  templateRepository: TemplateRepositoryPort,
                                  fileDocumentRepository: FileDocumentRepositoryPort
) extends FileDocumentUIPort with Actor with ActorLogging{

  override protected val fileDocumentService: FileDocumentService = new FileDocumentService(pdfMakerPort,templateRepository,fileDocumentRepository)

  override def receive: Receive = {
    case CreateFileDocumentCmd(documentTemplateName: String, documentDetails: Map[String,String],format: Format) => {
      log.debug(s"RECEIVED CMD CreateFileDocument(${documentTemplateName} ${format} ${documentDetails.map(m =>" key:"+m._1+"="+m._2+", ")})")
      (for{
        fileDocument <- createFileDocument(documentTemplateName,documentDetails,format)
      } yield FileDocumentMsg(fileDocument)).pipeTo(sender())
    }
    case GiveMeFileDocumentCmd(fileDocumentId) => {
      log.debug(s"RECEIVED CMD GiveMeFileDocumentCmd(${fileDocumentId})")
      (for{
        fileDocument <- findFileDocumentById(fileDocumentId)
      } yield FileDocumentMsg(fileDocument)).pipeTo(sender())
    }
  }

  override def createFileDocument(documentTemplateName: String, documentDetails: Map[String, String], format: Format): Future[FileDocument] = {
    format match {
      case Format.PDF => fileDocumentService.createNewPDFFileDocument(documentTemplateName, documentDetails)
      case Format.DOC => throw new NotImplementedError()(s"File format ${Format.DOC} not implemented")
      case Format.JPG => throw new NotImplementedError()(s"File format ${Format.JPG} not implemented")
    }
  }

  def findFileDocumentById(fileDocumentId: String): Future[FileDocument] ={
    fileDocumentService.giveMeFileDocument(fileDocumentId)
  }


}