package com.abaddon83.legal.contracts.ports

import com.abaddon83.legal.contracts.domainModels.DDMandate
import com.abaddon83.legal.contracts.domainModels.FileRepositories.FileRepository
import com.abaddon83.legal.sharedValueObjects.contracts.Format

import scala.concurrent.Future

trait DocumentPort {

  def createDocument(ddMandate: DDMandate, format: Format) : Future[FileRepository]
}
