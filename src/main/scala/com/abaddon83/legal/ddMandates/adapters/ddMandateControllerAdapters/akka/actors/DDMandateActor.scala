package com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.akka.actors

import akka.actor.{Actor, ActorSystem, Props}
import com.abaddon83.legal.ddMandates.adapters.CreditorAdapters.fake.FakeCreditorAdapter
import com.abaddon83.legal.ddMandates.adapters.bankAccountAdapters.fake.FakeBankAccountAdapter
import com.abaddon83.legal.ddMandates.adapters.contractDDMandateAdapters.internal.ContractDDMandateInternalAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.DDMandateAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.akka.actors.commands.GiveMeDDMandateCmd
import com.abaddon83.legal.ddMandates.adapters.ddMandateControllerAdapters.akka.actors.responses.DDMandateMsg
import com.abaddon83.legal.ddMandates.adapters.ddMandateRepositoryAdapters.fake.FakeDDMandateRepositoryAdapter
import com.abaddon83.legal.ddMandates.ports.{BankAccountPort, ContractDDMandatePort, CreditorPort, DDMandateRepositoryPort}

import scala.concurrent.ExecutionContext.Implicits.global

class DDMandateActor(implicit actorSystem: ActorSystem ) extends DDMandateAdapter with Actor{

  override val bankAccountPort: BankAccountPort = new FakeBankAccountAdapter() //bind[BankAccountPort]
  override val contractPort :ContractDDMandatePort = new ContractDDMandateInternalAdapter() //bind[ContractDDMandatePort]
  override val creditorPort : CreditorPort = new FakeCreditorAdapter()//bind[CreditorPort]
  override val ddMandateRepositoryePort : DDMandateRepositoryPort = new FakeDDMandateRepositoryAdapter()//bind[DDMandateRepositoryPort]

  override def receive: Receive = {
    case GiveMeDDMandateCmd(ddMandateUUID) => {
      println("MSG AKKA RICEVUTO")
      for{
        ddmandate <- findByIdDDMandate(ddMandateUUID)
      } yield sender ! DDMandateMsg(ddmandate)
    }
    case _ => println("MSG AKKA NON RICOSCIUTO?")
  }
}

object DDMandateActor {
  def props()(implicit actorSystem: ActorSystem) = Props(new DDMandateActor())
}