package com.abaddon83.legal.ddMandates.ports

import wvlet.airframe._


trait DIPortsBinding {

  val bankAccountPort = bind[BankAccountPort]
  val contractPort = bind[DDMandateContractPort]
  val creditorPort = bind[CreditorPort]
  val ddMandateRepositoryePort = bind[DDMandateRepositoryPort]
}

