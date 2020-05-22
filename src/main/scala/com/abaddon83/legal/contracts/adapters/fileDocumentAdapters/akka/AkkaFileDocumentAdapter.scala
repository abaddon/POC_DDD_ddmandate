package com.abaddon83.legal.contracts.adapters.fileDocumentAdapters.akka

import akka.actor.{ActorSelection, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.abaddon83.legal.contracts.domainModels.DDMandate
import com.abaddon83.legal.contracts.domainModels.FileRepositories.FileRepository
import com.abaddon83.legal.contracts.ports.FileDocumentPort
import com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapter.akka.commands.CreateFileDocumentCmd
import com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapter.akka.messages.FileDocumentMsg
import com.abaddon83.legal.sharedValueObjects.contracts.Format

import scala.collection.immutable.HashMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps


class AkkaFileDocumentAdapter(implicit actorSystem: ActorSystem ) extends FileDocumentPort{
  implicit val timeout: Timeout = Timeout(5 seconds)
  val actorName = "fileDocumentActor"

  override def createDocument(ddMandate: DDMandate, format: Format): Future[FileRepository] = {
    val templateName = "FakeTemplate.pdf"
    for {
      ddMandateView <- ask(getActor(actorName), CreateFileDocumentCmd(templateName,getDocumentDetails(ddMandate),format)).mapTo[FileDocumentMsg]
    } yield convertFileDocumentMsgToFileRepository(ddMandateView)
  }


  private def getActor(actorName: String): ActorSelection = {
    actorSystem.actorSelection(s"akka://DDMandate/user/${actorName}")
  }

  private def getDocumentDetails(ddMandate: DDMandate): Map[String,String] = {
    HashMap("name3[first]"->ddMandate.identity.toString, "name3[last]"->"MyLastName")
  }

  private def convertFileDocumentMsgToFileRepository(fileDocumentMsg: FileDocumentMsg): FileRepository ={
    new FileRepository {
      override val provider: String = "internal"
      override def url: String = fileDocumentMsg.id
    }
  }

}
