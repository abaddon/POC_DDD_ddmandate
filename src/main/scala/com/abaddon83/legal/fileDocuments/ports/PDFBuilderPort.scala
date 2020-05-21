package com.abaddon83.legal.fileDocuments.ports

import com.abaddon83.legal.fileDocuments.domainModels.DocumentTemplate

import scala.concurrent.Future

trait PDFBuilderPort {
  def createFile(template: DocumentTemplate, templateData: Map[String, String]): Future[Array[Byte]]

}