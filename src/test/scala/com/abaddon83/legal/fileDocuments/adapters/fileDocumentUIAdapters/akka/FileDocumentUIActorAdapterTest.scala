package com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapters.akka

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import com.abaddon83.legal.fileDocuments.adapters.documentTemplateRepositoryAdapters.fake.FileDocumentTemplateRepositoryFakeAdapter
import com.abaddon83.legal.fileDocuments.adapters.fileBodyAdapters.fake.FileBodyFakeAdapter
import com.abaddon83.legal.fileDocuments.adapters.fileDocumentRepositoryAdapters.fake.FileDocumentRepositoryFakeAdapter
import com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapters.akka.commands.{CreateFileDocumentCmd, GiveMeFileDocumentCmd}
import com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapters.akka.messages.FileDocumentMsg
import com.abaddon83.legal.fileDocuments.ports.{FileBodyPort, FileDocumentRepositoryPort, FileDocumentTemplateRepositoryPort}
import com.abaddon83.legal.shares.contracts.Format.{DOC, PDF}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuiteLike

import scala.concurrent.duration._
import scala.language.postfixOps

class FileDocumentUIActorAdapterTest extends TestKit(ActorSystem("fileDocument")) with AnyFunSuiteLike with ImplicitSender with BeforeAndAfterAll with ScalaFutures {

  implicit  val pdfMakerPort: FileBodyPort = new FileBodyFakeAdapter()
  implicit  val templateRepository: FileDocumentTemplateRepositoryPort = new FileDocumentTemplateRepositoryFakeAdapter()
  implicit  val fileDocumentRepository: FileDocumentRepositoryPort = new FileDocumentRepositoryFakeAdapter()
  implicit val timeout: Timeout = Timeout(5 seconds)

  val fileDocumentActor = system.actorOf(FileDocumentUIActorAdapter.props(),name = "fileDocumentActor")


  test("send cmd CreateFileDocumentCmd"){

    val fileDocumentMsg = ask(fileDocumentActor,CreateFileDocumentCmd("ddMandate",Map("field"->"value"),PDF)).mapTo[FileDocumentMsg].futureValue

    assert((fileDocumentMsg.fileBinaries.map(_.toChar)).mkString == "fakeFile")
    assert(fileDocumentMsg.format == "PDF")

  }

  test("send cmd GiveMeFileDocumentCmd"){

    val id = ask(fileDocumentActor,CreateFileDocumentCmd("ddMandate",Map("field"->"value"),PDF)).mapTo[FileDocumentMsg].futureValue.id

    val fileDocumentMsg = ask(fileDocumentActor,GiveMeFileDocumentCmd(id)).mapTo[FileDocumentMsg].futureValue

    assert((fileDocumentMsg.fileBinaries.map(_.toChar)).mkString == "fakeFile")
    assert(fileDocumentMsg.format == "PDF")
    assert(fileDocumentMsg.id == id)
  }

  test("send cmd CreateFileDocumentCmd with wrong template"){
    assert(ask(fileDocumentActor,CreateFileDocumentCmd("wrongTemplate",Map("field"->"value"),PDF)).mapTo[FileDocumentMsg].failed.futureValue.isInstanceOf[NoSuchElementException])
  }

  test("send cmd GiveMeFileDocumentCmd with wrong formatted id"){
    assert(ask(fileDocumentActor,GiveMeFileDocumentCmd("wrongFormattedId.pdf")).mapTo[FileDocumentMsg].failed.futureValue.isInstanceOf[IllegalArgumentException])
  }

  test("send cmd GiveMeFileDocumentCmd with well formatted id that doesn't exist"){
    assert(ask(fileDocumentActor,GiveMeFileDocumentCmd("afb332ee-f518-40a6-9982-c9f6a114e8df.pdf")).mapTo[FileDocumentMsg].failed.futureValue.isInstanceOf[NoSuchElementException])
  }

  test("send cmd GiveMeFileDocumentCmd with well formatted id with a format not managed"){
    assert(ask(fileDocumentActor,GiveMeFileDocumentCmd("afb332ee-f518-40a6-9982-c9f6a114e8df.doc")).mapTo[FileDocumentMsg].failed.futureValue.isInstanceOf[NoSuchElementException])
  }

  test("send cmd CreateFileDocumentCmd with a format not supported"){
    ask(fileDocumentActor,CreateFileDocumentCmd("ddMandate",Map("field"->"value"),DOC)).mapTo[FileDocumentMsg].failed.futureValue.isInstanceOf[NotImplementedError]
  }


  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

}
