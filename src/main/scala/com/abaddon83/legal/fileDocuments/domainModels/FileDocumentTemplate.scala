package com.abaddon83.legal.fileDocuments.domainModels

import com.abaddon83.legal.fileDocuments.services.TemplateConfig

case class FileDocumentTemplate private(
                             templateConfig: TemplateConfig,
                             filledData: Map[String, String],
                             body: Array[Byte]
                           )

object FileDocumentTemplate{
  def build(templateConfig: TemplateConfig, filledData: Map[String, String], body: Array[Byte]): FileDocumentTemplate = {
    assert(templateConfig.fields.forall(filledData.contains(_)),s"The ${templateConfig.templateName} template needs these fields: [${templateConfig.fields.mkString(",")}]")
    FileDocumentTemplate(templateConfig,filledData,body)

  }
}