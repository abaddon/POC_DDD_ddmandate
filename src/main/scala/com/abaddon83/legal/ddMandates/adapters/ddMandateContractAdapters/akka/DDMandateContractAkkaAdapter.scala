package com.abaddon83.legal.ddMandates.adapters.ddMandateContractAdapters.akka

import akka.actor.{ActorSelection, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.abaddon83.legal.contracts.adapters.contractUIAdapters.actors.commands.{CreateDDMandateContractCmd, GiveMeSignedContractCmd}
import com.abaddon83.legal.contracts.adapters.contractUIAdapters.actors.responses.ContractMsg
import com.abaddon83.legal.ddMandates.domainModels.{DDMandate, DDMandateContract}
import com.abaddon83.legal.ddMandates.ports.DDMandateContractPort
import com.abaddon83.legal.shares.contracts.{ContractIdentity, DD_MANDATE, Format}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class DDMandateContractAkkaAdapter(implicit actorSystem: ActorSystem ) extends DDMandateContractPort {
  implicit val timeout: Timeout = Timeout(5 seconds)
  val actorName="contractActor"

  override def findSignedContractByContractId(contractIdentity: ContractIdentity): Future[DDMandateContract] = {
    for {
      contractSigned <- ask(getActor(actorName), GiveMeSignedContractCmd(contractIdentity)).mapTo[ContractMsg]
    } yield ddMandateContractFactory(contractSigned)
  }

  override def createContract(ddMandate: DDMandate): Future[DDMandateContract] = {
    for {
        contractMsg <- ask(getActor(actorName), CreateDDMandateContractCmd(ddMandate.identity)).mapTo[ContractMsg]
    } yield ddMandateContractFactory(contractMsg)
  }

  private def getActor(actorName: String): ActorSelection = {
    actorSystem.actorSelection(s"akka://DDMandate/user/${actorName}")
  }

  private def ddMandateContractFactory( contract: ContractMsg): DDMandateContract = {
    println("build DDMandateContract")
    DDMandateContract(ContractIdentity(contract.id),contract.reference,DD_MANDATE,contract.name,Format.PDF,contract.creationDate,contract.signatureDate)
  }
}




