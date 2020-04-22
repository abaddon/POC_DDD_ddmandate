package com.abaddon83.legal.ports

import com.abaddon83.legal.domainModel.contract.FileRepositories.FileRepository
import com.abaddon83.legal.domainModel.ddMandates.DDMandate

trait FileRepositoryPort {

  def createUnsignedDDMandate(ddMandate: DDMandate) : Option[FileRepository]
}
