package com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.akka.actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.pattern.pipe
import com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.DDMandateAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.akka.actors.commands.GiveMeDDMandateCmd
import com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.akka.actors.responses.DDMandateMsg
import com.abaddon83.legal.ddMandates.ports.{BankAccountPort, ContractDDMandatePort, CreditorPort, DDMandateRepositoryPort}

import scala.concurrent.ExecutionContext.Implicits.global

class DDMandateActor(implicit
                     actorSystem: ActorSystem,
                     bankAccountAdapter: BankAccountPort,
                     contractAdapter :ContractDDMandatePort,
                     creditorAdapter : CreditorPort,
                     ddMandateRepositoryAdapter : DDMandateRepositoryPort
                    ) extends DDMandateAdapter with Actor with ActorLogging{

  override val bankAccountPort: BankAccountPort = bankAccountAdapter
  override val contractPort :ContractDDMandatePort = contractAdapter
  override val creditorPort : CreditorPort = creditorAdapter
  override val ddMandateRepositoryPort : DDMandateRepositoryPort = ddMandateRepositoryAdapter

  override def receive: Receive = {

    case GiveMeDDMandateCmd(ddMandateUUID) => {
      log.debug(s"RECEIVED CMD GiveMeDDMandateCmd(${ddMandateUUID})")
      (for{
        ddMandate <- findByIdDDMandate(ddMandateUUID)
      } yield DDMandateMsg(ddMandate)).pipeTo(sender())
    }

    case cmd => log.info(s"RECEIVED CMD ${cmd.getClass().getTypeName} NOT RECOGNISED")
  }
}

object DDMandateActor {
  def props()(implicit
              actorSystem: ActorSystem,
              bankAccountAdapter: BankAccountPort,
              contractAdapter :ContractDDMandatePort,
              creditorAdapter : CreditorPort,
              ddMandateRepositoryAdapter : DDMandateRepositoryPort) = Props(new DDMandateActor())
}