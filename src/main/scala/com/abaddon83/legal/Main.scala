package com.abaddon83.legal

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.abaddon83.legal.contracts.ContractBindedAdapters
import com.abaddon83.legal.contracts.adapters.contractAdapters.akkaHttp.ContractRoutes
import com.abaddon83.legal.ddMandates.DDMandateBindedAdapters
import com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp.DDMandateRoutes
import com.abaddon83.libs.akkaHttp.AkkaHttpServer
import com.abaddon83.libs.akkaHttp.routes.RoutesBuilder
import wvlet.airframe._


object Main extends App with ContractBindedAdapters  with DDMandateBindedAdapters {

  override implicit val actorSystem: ActorSystem = ActorSystem(name = "DDMandate")
  override implicit val materializer: ActorMaterializer = ActorMaterializer()

  val adapterBinded = newDesign.add(getContractBind()).add(getDDMandateBind())

  adapterBinded.build[ServerAkkaHttp]{ app: ServerAkkaHttp =>
    // Do something with app
    ...
  }

  override val apiRoutes: RoutesBuilder = new RoutesBuilder(
    List(
      new DDMandateRoutes().getRoute(),
      new ContractRoutes().getRoute()
    )
  )





}
