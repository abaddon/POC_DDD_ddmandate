package com.abaddon83.legal.adapters.ddMandateAdapters.AkkaHttp.messages

import java.util.UUID


case class CreateDDMandate(bankAccountId: UUID, legalEntity: String)

