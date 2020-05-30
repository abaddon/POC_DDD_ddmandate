package com.abaddon83.legal.fileDocuments.domainModels

import com.abaddon83.legal.fileDocuments.services.TemplateConfig
import org.scalatest.funsuite.AnyFunSuite

class FileDocumentTemplateTest extends AnyFunSuite {

  test("new file document template") {

    val body = Array[Byte]("1".toByte)
    val templateName = "test"
    val fileName = "test.pdf"
    val fieldList = List("field1","field2")
    val filledDate = Map("field1" -> "val1","field2"-> "val2")
    
    val templateConfig = TemplateConfig(templateName,fileName,fieldList)
    val fileDocumentTemplate = FileDocumentTemplate.build(templateConfig,filledDate,body)

    assert(fileDocumentTemplate.filledData == filledDate)
    assert(fileDocumentTemplate.templateConfig == templateConfig)
    assert(fileDocumentTemplate.body == body)

  }

  test("new file document template with missing fields") {

    val body = Array[Byte]("1".toByte)
    val templateName = "test"
    val fileName = "test.pdf"
    val fieldList = List("field1","field2")
    val filledDate = Map("field1" -> "val1")

    val templateConfig = TemplateConfig(templateName,fileName,fieldList)
    assertThrows[AssertionError]{
      FileDocumentTemplate.build(templateConfig,filledDate,body)
    }
  }

  test("new file document template with more fields") {

    val body = Array[Byte]("1".toByte)
    val templateName = "test"
    val fileName = "test.pdf"
    val fieldList = List("field1","field2")
    val filledDate = Map("field1" -> "val1","field2"-> "val2","field3"-> "val3")

    val templateConfig = TemplateConfig(templateName,fileName,fieldList)
    val fileDocumentTemplate = FileDocumentTemplate.build(templateConfig,filledDate,body)

    assert(fileDocumentTemplate.filledData == filledDate)
    assert(fileDocumentTemplate.templateConfig == templateConfig)
    assert(fileDocumentTemplate.body == body)
  }
}
