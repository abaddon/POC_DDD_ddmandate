package com.abaddon83.legal.sharedValueObjects.fileDocuments

import java.util.UUID

import com.abaddon83.legal.sharedValueObjects.contracts.Format
import com.abaddon83.libs.ddd.AggregateStringIdentity


case class FileDocumentIdentity (private val uuid:UUID, val format: Format) extends AggregateStringIdentity{

  override protected val id: String = format match {
    case Format.PDF => s"${uuid.toString()}.pdf"
    case Format.DOC =>s"${uuid.toString()}.doc"
    case Format.JPG =>s"${uuid.toString()}.jpg"
  }
}

object FileDocumentIdentity {
  def apply(format: Format): FileDocumentIdentity = {
    new FileDocumentIdentity(UUID.randomUUID(),format)

  }
  def apply(fileName: String): FileDocumentIdentity = {
    val subString = fileName.split(".")
    assert(subString.size == 2, "FileName not recognised")
    val uuid=UUID.fromString(subString(0))
    val format=Format.valueOf(subString(1))
    new FileDocumentIdentity(uuid,format)
  }
}