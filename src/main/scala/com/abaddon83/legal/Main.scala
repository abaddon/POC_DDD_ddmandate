package com.abaddon83.legal

/* with DI
object Main extends App with ContractBindedAdapters  with DDMandateBindedAdapters {

  val adaptersBound = newDesign.add(contractBindDesign).add(ddMandateBindDesign)

  adaptersBound.withSession { session =>
    val app = session.build[AkkaHttpServer]
    app.startServer()
  }

}
*/

object Main extends App with AkkaHttpServer{



    startServer()


}
