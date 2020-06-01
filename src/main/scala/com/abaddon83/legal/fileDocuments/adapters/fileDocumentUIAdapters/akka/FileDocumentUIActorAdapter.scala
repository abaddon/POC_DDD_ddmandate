package com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapters.akka

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern._
import com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapters.akka.commands.{CreateFileDocumentCmd, GiveMeFileDocumentCmd}
import com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapters.akka.messages.FileDocumentMsg
import com.abaddon83.legal.fileDocuments.domainModels.FileDocument
import com.abaddon83.legal.fileDocuments.ports.{FileBodyPort, FileDocumentRepositoryPort, FileDocumentTemplateRepositoryPort, FileDocumentUIPort}
import com.abaddon83.legal.fileDocuments.services.FileDocumentService
import com.abaddon83.legal.shares.contracts.Format
import com.abaddon83.legal.shares.fileDocuments.FileDocumentIdentity
import com.abaddon83.libs.akkaActors.FailurePropatingActor
import sun.reflect.generics.reflectiveObjects.NotImplementedException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FileDocumentUIActorAdapter()(implicit pdfMakerPort: FileBodyPort,
                                   templateRepository: FileDocumentTemplateRepositoryPort,
                                   fileDocumentRepository: FileDocumentRepositoryPort
) extends FileDocumentUIPort with Actor with ActorLogging with FailurePropatingActor{

  override protected val fileDocumentService: FileDocumentService = new FileDocumentService(pdfMakerPort,templateRepository,fileDocumentRepository)

  override def receive: Receive = {
    case CreateFileDocumentCmd(documentTemplateName: String, documentDetails: Map[String,String],format: Format) => {
      log.debug(s"RECEIVED CMD CreateFileDocument(${documentTemplateName} ${format} ${documentDetails.map(m =>" key:"+m._1+"="+m._2+", ")})")
      val result = for{
          fileDocument <- createFileDocument(documentTemplateName,documentDetails,format)
        } yield FileDocumentMsg(fileDocument)
      result.pipeTo(sender())
    }
    case GiveMeFileDocumentCmd(fileDocumentId) => {
      log.debug(s"RECEIVED CMD GiveMeFileDocumentCmd(${fileDocumentId})")
      val result =for{
        fileDocument <- findFileDocumentById(fileDocumentId)
      } yield FileDocumentMsg(fileDocument)
      result.pipeTo(sender())
    }
    //case Status.Failure(ex) => throw ex
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
              templateRepository: FileDocumentTemplateRepositoryPort,
              fileDocumentRepository: FileDocumentRepositoryPort
  ) = Props(new FileDocumentUIActorAdapter())
}
