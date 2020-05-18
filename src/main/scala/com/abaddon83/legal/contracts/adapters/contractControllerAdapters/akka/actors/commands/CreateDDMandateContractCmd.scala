package com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors.commands

import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity

case class CreateDDMandateContractCmd(DDMandateIdentity: DDMandateIdentity)
