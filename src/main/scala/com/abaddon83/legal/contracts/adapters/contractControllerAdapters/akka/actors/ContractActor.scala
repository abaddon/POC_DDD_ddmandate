package com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.abaddon83.legal.contracts.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.FileRepositoryAdapters.Fake.FakeFileRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.ContractAdapter
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors.commands.{CreateDDMandateContractCmd, GiveMeSignedContractCmd}
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.akka.actors.responses.ContractMsg
import com.abaddon83.legal.contracts.adapters.ddMandateAdapters.internal.DDMandateInternalAdapter
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, DDMandatePort, FileRepositoryPort}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class ContractActor()(implicit actorSystem: ActorSystem ) extends ContractAdapter with Actor with ActorLogging{

  override val ddMandatePort : DDMandatePort = new DDMandateInternalAdapter //bind[DDMandatePort]
  override val contractRepositoryPort : ContractRepositoryPort = new FakeContractRepositoryAdapter //bind[ContractRepositoryPort]
  override val fileRepositoryPort : FileRepositoryPort = new FakeFileRepositoryAdapter //bind[FileRepositoryPort]

  override def receive: Receive = {
    case GiveMeSignedContractCmd(contractIdentity) => {
      log.info("MSG GiveMeSignedContractCmd RICEVUTO")
      log.debug(s"contractIdentity: ${contractIdentity.uuid}")
      for{
        contractSigned <- findSignedContractByIdContract(contractIdentity.uuid)
      } yield sender ! ContractMsg(contractSigned)
    }
    case CreateDDMandateContractCmd(dDMandateIdentity) => {
      log.info("MSG CreateDDMandateContractCmd RICEVUTO")
      log.debug(s"CreateDDMandateContractCmd -> dDMandateIdentity: ${dDMandateIdentity.uuid}")
      //val contract=ContractUnSigned(ContractIdentity(),DD_MANDATE,dDMandateIdentity.uuid.toString,"CIAO",PDF,S3FileRepository("",""),new Date())
      //
      //val contractUnsigned = createContract("DDMANDATE", dDMandateIdentity.uuid)

        createContract("DDMANDATE", dDMandateIdentity.uuid) onComplete{
          case Success(value) => sender ! value
        }

        //contractMsg = ContractMsg(contractUnsigned)

      //contractUnsigned.flatMap(contract => Future(ContractMsg(contract))).pipeTo(sender)
    }
    case _ => println("MSG AKKA NON RICOSCIUTO?")
  }
}

object ContractActor {
  def props()(implicit actorSystem: ActorSystem ) = Props(new ContractActor())
}

