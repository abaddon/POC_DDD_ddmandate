package com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.pattern.pipe
import com.abaddon83.legal.contracts.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.FileRepositoryAdapters.Fake.FakeFileRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.ContractAdapter
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors.commands.{CreateDDMandateContractCmd, GiveMeSignedContractCmd}
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors.responses.ContractMsg
import com.abaddon83.legal.contracts.adapters.ddMandateAdapters.internal.DDMandateInternalAdapter
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, DDMandatePort, FileRepositoryPort}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

class ContractActor()(implicit actorSystem: ActorSystem ) extends ContractAdapter with Actor with ActorLogging{

  override val ddMandatePort : DDMandatePort = new DDMandateInternalAdapter //bind[DDMandatePort]
  override val contractRepositoryPort : ContractRepositoryPort =  FakeContractRepositoryAdapter //bind[ContractRepositoryPort]
  override val fileRepositoryPort : FileRepositoryPort = new FakeFileRepositoryAdapter //bind[FileRepositoryPort]

  override def receive: Receive = {

    case GiveMeSignedContractCmd(contractIdentity) => {
      log.info(s"RECEVICED CMD GiveMeSignedContractCmd(${contractIdentity}")
      (for{
        contractSigned <- findSignedContractByIdContract(contractIdentity.uuid)
      } yield ContractMsg(contractSigned)).pipeTo(sender())
    }

    case CreateDDMandateContractCmd(dDMandateIdentity) => {
      log.info(s"RECEVICED CMD CreateDDMandateContractCmd(${dDMandateIdentity})")
      (for{
        contractUnSigned <- createContract("DDMANDATE", dDMandateIdentity.uuid)
      } yield ContractMsg(contractUnSigned)).pipeTo(sender())
    }

    case cmd => log.debug(s"RECEIVED CMD ${cmd.getClass().getTypeName} NOT RECOGNISED")
  }
}

object ContractActor {
  def props()(implicit actorSystem: ActorSystem ) = Props(new ContractActor())
}

