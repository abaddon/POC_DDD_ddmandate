package com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp.messages

import java.util.UUID


case class CreateDDMandate(bankAccountId: UUID, legalEntity: String)

