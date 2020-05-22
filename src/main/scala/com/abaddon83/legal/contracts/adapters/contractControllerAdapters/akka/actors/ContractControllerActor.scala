package com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.pattern.pipe
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.ContractControllerAdapter
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors.commands.{CreateDDMandateContractCmd, GiveMeSignedContractCmd}
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors.responses.ContractMsg
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, DDMandatePort, DocumentPort}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

class ContractControllerActor()(implicit
                                actorSystem: ActorSystem,
                                ddMandateAdapter : DDMandatePort,
                                contractRepositoryAdapter : ContractRepositoryPort,
                                fileRepositoryAdapter : DocumentPort
) extends ContractControllerAdapter with Actor with ActorLogging{

  override val ddMandatePort : DDMandatePort = ddMandateAdapter
  override val contractRepositoryPort : ContractRepositoryPort =  contractRepositoryAdapter
  override val fileRepositoryPort : DocumentPort = fileRepositoryAdapter


  override def receive: Receive = {

    case GiveMeSignedContractCmd(contractIdentity) => {
      log.info(s"RECEVICED CMD GiveMeSignedContractCmd(${contractIdentity}")
      (for{
        contractSigned <- findSignedContractByIdContract(contractIdentity.convertTo())
      } yield ContractMsg(contractSigned)).pipeTo(sender())
    }

    case CreateDDMandateContractCmd(dDMandateIdentity) => {
      log.info(s"RECEVICED CMD CreateDDMandateContractCmd(${dDMandateIdentity})")
      (for{
        contractUnSigned <- createContract("DDMANDATE", dDMandateIdentity.convertTo())
      } yield ContractMsg(contractUnSigned)).pipeTo(sender())
    }

    case cmd => log.info(s"RECEIVED CMD ${cmd.getClass().getTypeName} NOT RECOGNISED")
  }
}

object ContractControllerActor {
  def props()(implicit
              actorSystem: ActorSystem,
              ddMandateAdapter : DDMandatePort,
              contractRepositoryAdapter : ContractRepositoryPort,
              fileRepositoryAdapter : DocumentPort) = Props(new ContractControllerActor())
}

