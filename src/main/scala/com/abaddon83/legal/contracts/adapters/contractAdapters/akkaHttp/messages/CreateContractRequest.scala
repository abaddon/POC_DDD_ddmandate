package com.abaddon83.legal.contracts.adapters.contractAdapters.akkaHttp.messages

import java.util.UUID

case class CreateContractRequest(contractType: String, reference: UUID)

