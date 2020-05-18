package com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.pattern.pipe
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.ContractAdapter
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors.commands.{CreateDDMandateContractCmd, GiveMeSignedContractCmd}
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors.responses.ContractMsg
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, DDMandatePort, FileRepositoryPort}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

class ContractActor()(implicit
                      actorSystem: ActorSystem,
                      ddMandateAdapter : DDMandatePort,
                      contractRepositoryAdapter : ContractRepositoryPort,
                      fileRepositoryAdapter : FileRepositoryPort
) extends ContractAdapter with Actor with ActorLogging{

  override val ddMandatePort : DDMandatePort = ddMandateAdapter
  override val contractRepositoryPort : ContractRepositoryPort =  contractRepositoryAdapter
  override val fileRepositoryPort : FileRepositoryPort = fileRepositoryAdapter

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

    case cmd => log.info(s"RECEIVED CMD ${cmd.getClass().getTypeName} NOT RECOGNISED")
  }
}

object ContractActor {
  def props()(implicit
              actorSystem: ActorSystem,
              ddMandateAdapter : DDMandatePort,
              contractRepositoryAdapter : ContractRepositoryPort,
              fileRepositoryAdapter : FileRepositoryPort) = Props(new ContractActor())
}

