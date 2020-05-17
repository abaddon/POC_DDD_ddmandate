package com.abaddon83.legal.contracts.adapters.ddMandateAdapters.internal

import akka.actor.{ActorSelection, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.abaddon83.legal.contracts.domainModels.DDMandate
import com.abaddon83.legal.contracts.ports.DDMandatePort
import com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.akka.actors.commands.GiveMeDDMandateCmd
import com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.akka.actors.responses.DDMandateMsg
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class DDMandateInternalAdapter(implicit actorSystem: ActorSystem ) extends DDMandatePort {
  implicit val timeout: Timeout = Timeout(5 seconds)

  val actorName = "ddMandateActor"

  override def findDDMandateById(ddMandateIdentity: DDMandateIdentity): Future[DDMandate] = {
    for {
      ddMAndateView <- ask(getActor(actorName), GiveMeDDMandateCmd(ddMandateIdentity.uuid)).mapTo[DDMandateMsg]
    } yield convertDDMandateViewToDDMandate(ddMAndateView)
  }

  private def convertDDMandateViewToDDMandate(ddmandateView: DDMandateMsg): DDMandate ={
      DDMandate(DDMandateIdentity(ddmandateView.id))
  }

  private def getActor(actorName: String): ActorSelection = {
    actorSystem.actorSelection(s"akka://DDMandate/user/${actorName}")
  }

}
