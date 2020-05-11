package com.abaddon83.legal.contracts.ports

import com.abaddon83.legal.contracts.domainModels.DDMandate
import com.abaddon83.legal.contracts.domainModels.FileRepositories.FileRepository

trait FileRepositoryPort {

  def createUnsignedDDMandate(ddMandate: DDMandate) : Option[FileRepository]
}
