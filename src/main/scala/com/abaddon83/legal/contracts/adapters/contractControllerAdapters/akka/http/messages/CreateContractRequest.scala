package com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.http.messages

import java.util.UUID

case class CreateContractRequest(contractType: String, reference: UUID)

