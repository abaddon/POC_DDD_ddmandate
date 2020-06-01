package com.abaddon83.legal.contracts.adapters.contractUIAdapters.actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.pattern.pipe
import com.abaddon83.legal.contracts.adapters.contractUIAdapters.ContractUIAdapter
import com.abaddon83.legal.contracts.adapters.contractUIAdapters.actors.commands.{CreateDDMandateContractCmd, GiveMeSignedContractCmd}
import com.abaddon83.legal.contracts.adapters.contractUIAdapters.actors.responses.ContractMsg
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, DDMandatePort, FileDocumentPort}
import com.abaddon83.libs.akkaActors.FailurePropatingActor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

class ContractUIActor()(implicit
                        actorSystem: ActorSystem,
                        ddMandateAdapter : DDMandatePort,
                        contractRepositoryAdapter : ContractRepositoryPort,
                        fileRepositoryAdapter : FileDocumentPort
) extends ContractUIAdapter with Actor with ActorLogging with FailurePropatingActor{

  override val ddMandatePort : DDMandatePort = ddMandateAdapter
  override val contractRepositoryPort : ContractRepositoryPort =  contractRepositoryAdapter
  override val fileRepositoryPort : FileDocumentPort = fileRepositoryAdapter


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

object ContractUIActor {
  def props()(implicit
              actorSystem: ActorSystem,
              ddMandateAdapter : DDMandatePort,
              contractRepositoryAdapter : ContractRepositoryPort,
              fileRepositoryAdapter : FileDocumentPort) = Props(new ContractUIActor())
}

