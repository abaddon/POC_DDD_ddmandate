package com.abaddon83.legal.fileDocuments.services

import com.typesafe.config.{ConfigException, ConfigFactory}

import scala.jdk.CollectionConverters._

case object FileDocumentConfigService{
  private val fileDocumentsConfig = ConfigFactory.load("application.conf").getConfig("com.abaddon83.legal.fileDocuments")

  def getTemplateConfig(templateName: String): TemplateConfig = {
    try {
      val templateConfig = fileDocumentsConfig.getConfig(s"templates.$templateName")
      val name = templateConfig.getString("fileName")
      val fields = templateConfig.getStringList("fields").asScala.toList
      TemplateConfig(templateName,name,fields)
    } catch {
      case ex : ConfigException.Missing => throw new NoSuchElementException(s"Templates $templateName doesn't exist")
    }

  }

  def getFileRepositoryPath(): String = {
    fileDocumentsConfig.getString("fileRepositoryPath")
  }

  def getTemplatePath(): String = {
    fileDocumentsConfig.getString("templatePath")
  }
}


case class TemplateConfig(templateName: String, fileName: String, fields: List[String])