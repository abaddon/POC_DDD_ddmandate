package com.abaddon83.legal.contracts.ports

import wvlet.airframe._


trait DIPortsBinding {

  val ddMandatePort = bind[DDMandatePort]
  val contractRepositoryPort = bind[ContractRepositoryPort]
  val fileRepositoryPort = bind[FileRepositoryPort]
}


