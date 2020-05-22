package com.abaddon83.legal.fileDocuments.adapters.templateRepositoryAdapters.fake

import java.nio.file.{Files, Paths}

import com.abaddon83.legal.fileDocuments.domainModels.DocumentTemplate
import com.abaddon83.legal.fileDocuments.ports.TemplateRepositoryPort

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class FakeTemplateRepository extends TemplateRepositoryPort{

  var templatePath: String = "./fileRepository/templates"

  override def findTemplateByName(name: String): Future[DocumentTemplate] = {

    Future{
        val byteArray = Files.readAllBytes(Paths.get(s"$templatePath/$name"))
        DocumentTemplate(name,byteArray)
    }
  }
}
