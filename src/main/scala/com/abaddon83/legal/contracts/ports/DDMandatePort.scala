package com.abaddon83.legal.contracts.ports

import com.abaddon83.legal.contracts.domainModels.DDMandate
import com.abaddon83.legal.shares.ddMandates.DDMandateIdentity

import scala.concurrent.Future

trait DDMandatePort {

  def findDDMandateById(ddMandateIdentity: DDMandateIdentity): Future[DDMandate]

}
