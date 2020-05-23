package com.abaddon83.legal.ddMandates.adapters.ddMandateUIAdapters.akkaHttp.messages

import java.util.UUID


case class CreateDDMandateRequest(bankAccountId: UUID, legalEntity: String)

