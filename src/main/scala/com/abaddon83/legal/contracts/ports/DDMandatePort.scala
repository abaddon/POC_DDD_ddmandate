package com.abaddon83.legal.contracts.ports

import com.abaddon83.legal.contracts.domainModels.DDMandate
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity

trait DDMandatePort {

  def findDDMandateById(ddMandateIdentity: DDMandateIdentity): Option[DDMandate]

}
