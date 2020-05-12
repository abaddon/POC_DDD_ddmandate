package com.abaddon83.legal

import com.abaddon83.legal.contracts.ContractBindedAdapters
import com.abaddon83.legal.ddMandates.DDMandateBindedAdapters
import wvlet.airframe._


object Main extends App with ContractBindedAdapters  with DDMandateBindedAdapters {

  val adaptersBound = newDesign.add(contractBindDesign).add(ddMandateBindDesign)

  adaptersBound.withSession { session =>
    val app = session.build[AkkaHttpServer]
    app.startServer()
  }

}
