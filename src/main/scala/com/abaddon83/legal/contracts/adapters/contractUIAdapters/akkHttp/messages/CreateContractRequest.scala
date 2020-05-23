package com.abaddon83.legal.contracts.adapters.contractUIAdapters.akka.akkHttp.messages

import java.util.UUID

case class CreateContractRequest(contractType: String, reference: UUID)

