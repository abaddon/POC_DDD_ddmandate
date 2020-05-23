package com.abaddon83.legal.contracts.adapters.ddMandateAdapters.akka

import akka.actor.{ActorSelection, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.abaddon83.legal.contracts.domainModels.DDMandate
import com.abaddon83.legal.contracts.ports.DDMandatePort
import com.abaddon83.legal.ddMandates.adapters.ddMandateUIAdapters.akka.commands.GiveMeDDMandateCmd
import com.abaddon83.legal.ddMandates.adapters.ddMandateUIAdapters.akka.responses.DDMandateMsg
import com.abaddon83.legal.shares.ddMandates.DDMandateIdentity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class DDMandateAkkaAdapter(implicit actorSystem: ActorSystem ) extends DDMandatePort {
  implicit val timeout: Timeout = Timeout(5 seconds)
  val actorName = "ddMandateActor"

  override def findDDMandateById(ddMandateIdentity: DDMandateIdentity): Future[DDMandate] = {
    for {
      ddMandateView <- ask(getActor(actorName), GiveMeDDMandateCmd(ddMandateIdentity.convertTo())).mapTo[DDMandateMsg]
    } yield convertDDMandateViewToDDMandate(ddMandateView)
  }

  private def getActor(actorName: String): ActorSelection = {
    actorSystem.actorSelection(s"akka://DDMandate/user/${actorName}")
  }

  private def convertDDMandateViewToDDMandate(ddmandateView: DDMandateMsg): DDMandate ={
    DDMandate(DDMandateIdentity(ddmandateView.id))
  }

}
