package com.abaddon83.legal.ddMandates.adapters.ddMandateUIAdapters.akka.commands

import java.util.UUID

sealed trait Command {}
case class GiveMeDDMandateCmd(ddMandateUUID: UUID) extends Command
