package com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp.messages

import java.util.UUID


case class CreateDDMandateRequest(bankAccountId: UUID, legalEntity: String)

