package com.abaddon83.legal.fileDocuments.services

import com.typesafe.config.ConfigException
import org.scalatest.funsuite.AnyFunSuite

class FileDocumentConfigServiceTest extends AnyFunSuite{

  test(" load ddMandate template configuration") {
    val templateName = "ddMandate"
    val templateConfiguration: TemplateConfig = FileDocumentConfigService.getTemplateConfig(templateName)
    val expectedFields = List("name3[first]","name3[last]")
    val expectedFileName = "FakeTemplate.pdf"

    assert(templateConfiguration.templateName == templateName)
    assert(templateConfiguration.isInstanceOf[TemplateConfig])
    assert(templateConfiguration.fields.size == 2)
    assert(templateConfiguration.fields.contains(expectedFields(0)))
    assert(templateConfiguration.fields.contains(expectedFields(1)))
    assert(templateConfiguration.fileName == expectedFileName)
  }

  test(" load missing template configuration") {
    val templateName = "missing"
    assertThrows[ConfigException] {
      FileDocumentConfigService.getTemplateConfig(templateName)
    }
  }

  test(" load file repository path") {
    val expectedRepositoryPath = "./fileRepository/"

    assert(expectedRepositoryPath == FileDocumentConfigService.getFileRepositoryPath())
  }

  test(" load template path") {
    val expectedTemplatePath = "./fileRepository/templates/"

    assert(expectedTemplatePath == FileDocumentConfigService.getTemplatePath())
  }

}