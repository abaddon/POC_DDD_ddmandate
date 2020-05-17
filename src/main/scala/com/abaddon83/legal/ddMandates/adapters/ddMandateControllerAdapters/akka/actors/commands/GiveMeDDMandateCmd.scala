package com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.akka.actors.commands

import java.util.UUID

sealed trait Command {}
case class GiveMeDDMandateCmd(ddMandateUUID: UUID) extends Command
