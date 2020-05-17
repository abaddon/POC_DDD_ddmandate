package com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.akka.http.messages

import java.util.UUID


case class CreateDDMandateRequest(bankAccountId: UUID, legalEntity: String)

