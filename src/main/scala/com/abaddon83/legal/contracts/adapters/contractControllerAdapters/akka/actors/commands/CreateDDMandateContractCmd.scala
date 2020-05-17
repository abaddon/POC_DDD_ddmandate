package com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors.commands

import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity

final case class CreateDDMandateContractCmd(DDMandateIdentity: DDMandateIdentity)

//final case class Update(value: Customer, replyTo: ActorRef[UpdateResult]) extends Command
