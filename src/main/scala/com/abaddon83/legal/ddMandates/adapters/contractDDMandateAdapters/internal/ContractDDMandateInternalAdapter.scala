package com.abaddon83.legal.ddMandates.adapters.contractDDMandateAdapters.internal

import akka.actor.{ActorSelection, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors.commands.{CreateDDMandateContractCmd, GiveMeSignedContractCmd}
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors.responses.ContractMsg
import com.abaddon83.legal.contracts.domainModels.{Contract, ContractSigned, ContractUnSigned}
import com.abaddon83.legal.ddMandates.domainModels.{DDMandate, DDMandateContract}
import com.abaddon83.legal.ddMandates.ports.ContractDDMandatePort
import com.abaddon83.legal.sharedValueObjects.contracts.{ContractIdentity, DD_MANDATE, PDF}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

/*
class DDMandateInternalAdapter extends DDMandatePort {
  val actorSystem = ActorSystem("DDMandateAdapter")

  override def findDDMandateById(ddMandateIdentity: DDMandateIdentity): Future[DDMandate] = {
    val ddMandateActor=actorSystem.actorOf(Props[DDMandateActor],name = "ddMandateActor")
    implicit val timeout: Timeout = Timeout(5 seconds)
    for {
      ddMAndateView <- ask(ddMandateActor, GiveMeDDMandate(ddMandateIdentity.uuid)).mapTo[DDMandateView]
    } yield convertDDMandateViewToDDMandate(ddMAndateView)
  }

  def convertDDMandateViewToDDMandate(ddmandateView: DDMandateView): DDMandate ={
      DDMandate(DDMandateIdentity(ddmandateView.id))
  }

}
 */

class ContractDDMandateInternalAdapter(implicit actorSystem: ActorSystem ) extends ContractDDMandatePort {

  implicit val timeout: Timeout = Timeout(10 seconds)

  val actorName="contractActor"

  override def findSignedContractByContractId(contractIdentity: ContractIdentity): Future[DDMandateContract] = {
    for {
      contractSigned <- ask(getActor(actorName), GiveMeSignedContractCmd(contractIdentity)).mapTo[ContractSigned]
    } yield convertToDDMandateContract(contractSigned)

  }

  override def createContract(ddMandate: DDMandate): Future[DDMandateContract] = {
    for {
        contractMsg <- ask(getActor(actorName), CreateDDMandateContractCmd(ddMandate.identity)).mapTo[ContractMsg]
    } yield ddMandateContractFactory(contractMsg)
  }

  private def getActor(actorName: String): ActorSelection = {
    actorSystem.actorSelection(s"akka://DDMandate/user/${actorName}")
  }

  private def convertToDDMandateContract( contact: Contract): DDMandateContract = {

    contact match {
      case contractUnSigned: ContractUnSigned => convertToDDMandateContract(contractUnSigned)
      case contractSigned: ContractSigned => convertToDDMandateContract(contractSigned)
    }
  }



  private def ddMandateContractFactory( contract: ContractMsg): DDMandateContract = {
    println("build DDMandateContract")
    DDMandateContract(ContractIdentity(contract.id),contract.reference,DD_MANDATE,contract.name,PDF,contract.creationDate,None)
  }

  private def convertToDDMandateContract( contract: ContractSigned): DDMandateContract = {
    DDMandateContract(contract.identity,contract.reference,contract.contractType,contract.name,contract.format,contract.creationDate,Some(contract.signatureDate))
  }


}




