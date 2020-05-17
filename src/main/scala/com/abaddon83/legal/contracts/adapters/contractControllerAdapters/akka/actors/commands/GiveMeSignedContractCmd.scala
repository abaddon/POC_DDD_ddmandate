package com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors.commands

import com.abaddon83.legal.sharedValueObjects.contracts.ContractIdentity

case class GiveMeSignedContractCmd(contractIdentity: ContractIdentity)
