package com.abaddon83.legal.ports

import com.abaddon83.legal.domainModel.contract.Contract
import com.abaddon83.legal.domainModel.ddMandates.DDMandate

trait ContractPort {
  def createDDMandateContract(DDMandate: DDMandate): Contract

}
