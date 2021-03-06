package com.abaddon83.legal.contracts.ports

import com.abaddon83.legal.contracts.domainModels.DDMandate
import com.abaddon83.legal.contracts.domainModels.FileRepositories.FileRepository
import com.abaddon83.legal.shares.contracts.Format

import scala.concurrent.Future

trait FileDocumentPort {

  def createDocument(ddMandate: DDMandate, format: Format) : Future[FileRepository]
}
