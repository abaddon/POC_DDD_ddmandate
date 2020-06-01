package com.abaddon83.legal.ddMandates.adapters.ddMandateUIAdapters.akka

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.pattern.pipe
import com.abaddon83.legal.ddMandates.adapters.ddMandateUIAdapters.DDMandateUIAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateUIAdapters.akka.commands.GiveMeDDMandateCmd
import com.abaddon83.legal.ddMandates.adapters.ddMandateUIAdapters.akka.responses.DDMandateMsg
import com.abaddon83.legal.ddMandates.ports.{BankAccountPort, CreditorPort, DDMandateContractPort, DDMandateRepositoryPort}
import com.abaddon83.libs.akkaActors.FailurePropatingActor

import scala.concurrent.ExecutionContext.Implicits.global

class DDMandateUIActor(implicit
                       actorSystem: ActorSystem,
                       bankAccountAdapter: BankAccountPort,
                       contractAdapter :DDMandateContractPort,
                       creditorAdapter : CreditorPort,
                       ddMandateRepositoryAdapter : DDMandateRepositoryPort
                    ) extends DDMandateUIAdapter with Actor with ActorLogging with FailurePropatingActor{

  override val bankAccountPort: BankAccountPort = bankAccountAdapter
  override val contractPort :DDMandateContractPort = contractAdapter
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

object DDMandateUIActor {
  def props()(implicit
              actorSystem: ActorSystem,
              bankAccountAdapter: BankAccountPort,
              contractAdapter :DDMandateContractPort,
              creditorAdapter : CreditorPort,
              ddMandateRepositoryAdapter : DDMandateRepositoryPort) = Props(new DDMandateUIActor())
}