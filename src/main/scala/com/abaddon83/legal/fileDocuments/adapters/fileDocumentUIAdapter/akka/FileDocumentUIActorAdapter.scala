package com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapter.akka

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern._
import com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapter.akka.commands.{CreateFileDocumentCmd, GiveMeFileDocumentCmd}
import com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapter.akka.messages.FileDocumentMsg
import com.abaddon83.legal.fileDocuments.domainModels.FileDocument
import com.abaddon83.legal.fileDocuments.ports.{FileDocumentRepositoryPort, FileDocumentUIPort, FileBodyPort, TemplateRepositoryPort}
import com.abaddon83.legal.fileDocuments.services.FileDocumentService
import com.abaddon83.legal.shares.contracts.Format
import com.abaddon83.legal.shares.fileDocuments.FileDocumentIdentity
import sun.reflect.generics.reflectiveObjects.NotImplementedException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FileDocumentUIActorAdapter()(implicit pdfMakerPort: FileBodyPort,
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
      case Format.DOC => throw new NotImplementedException()
      case Format.JPG => throw new NotImplementedException()
    }
  }

  def findFileDocumentById(fileDocumentId: String): Future[FileDocument] ={
    fileDocumentService.giveMeFileDocument(FileDocumentIdentity(fileDocumentId))
  }

  override def getFileDocument(fileDocumentIdentity: FileDocumentIdentity): Future[FileDocument] = ???
}

object FileDocumentUIActorAdapter {
  def props()(implicit pdfMakerPort: FileBodyPort,
              templateRepository: TemplateRepositoryPort,
              fileDocumentRepository: FileDocumentRepositoryPort
  ) = Props(new FileDocumentUIActorAdapter())
}
