package com.abaddon83.legal.fileDocuments.adapters.fileBodyAdapters.pdfBox

import java.io.ByteArrayOutputStream

import com.abaddon83.legal.fileDocuments.domainModels.DocumentTemplate
import com.abaddon83.legal.fileDocuments.ports.FileBodyPort
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.PDField

import scala.concurrent.Future
import scala.jdk.CollectionConverters._
import scala.concurrent.ExecutionContext.Implicits.global



class FileBodyPdfBoxAdapter extends FileBodyPort{

  override def createFile(template: DocumentTemplate, templateData: Map[String, String]): Future[Array[Byte]] = {
    Future{

      val pdfDocument = PDDocument.load(template.body)
      val pdfDocumentFilled = fillPdf(pdfDocument,templateData)

      val buffer = new ByteArrayOutputStream()
      pdfDocumentFilled.save(buffer)
      pdfDocumentFilled.close()
      buffer.toByteArray
    }
  }

  private def fillPdf(pdfDocument: PDDocument, templateData: Map[String, String]): PDDocument  = {
    val pdfForms = pdfDocument.getDocumentCatalog.getAcroForm

    pdfForms.getFields.asScala.toList.map { pdfField: PDField =>
      val pdfFieldName = pdfField.getFullyQualifiedName
      //println(s"pdfFieldName: ${pdfFieldName}")
      templateData.get(pdfFieldName).map { value =>
        pdfField.setValue(value)
        pdfField.setReadOnly(true)
      }
    }
    pdfDocument.getDocumentCatalog.setAcroForm(pdfForms)
    pdfDocument
  }


}
