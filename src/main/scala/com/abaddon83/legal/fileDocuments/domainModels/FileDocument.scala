package com.abaddon83.legal.fileDocuments.domainModels

import com.abaddon83.legal.shares.contracts.Format
import com.abaddon83.legal.shares.fileDocuments.FileDocumentIdentity

trait  FileDocument {
  val identity: FileDocumentIdentity
  val fileBinaries : Array[Byte]
  val format: Format
}
