package com.abaddon83.legal.contracts.adapters.contractAdapters

import java.util.UUID

import com.abaddon83.legal.contracts.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.contractControllerAdapters.ContractControllerAdapter
import com.abaddon83.legal.contracts.adapters.ddMandateAdapters.fake.FakeDDMandateAdapter
import com.abaddon83.legal.contracts.adapters.documentAdapters.fake.FakeDocumentAdapter
import com.abaddon83.legal.contracts.domainModels.ContractUnSigned
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, DDMandatePort, DocumentPort}
import com.abaddon83.legal.utilities.UUIDRegistryHelper
import com.abaddon83.libs.akkaHttp.messages.ErrorMessage
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ContractAdapterTest extends AnyFunSuite with Matchers with ScalaFutures {

  val contractAdapter = new ContractControllerAdapter() {
    override implicit val ddMandatePort: DDMandatePort = new FakeDDMandateAdapter
    override implicit val contractRepositoryPort: ContractRepositoryPort = new FakeContractRepositoryAdapter
    override implicit val fileRepositoryPort: DocumentPort = new FakeDocumentAdapter
  }

  test("create contract"){
    contractAdapter.ddMandatePort.asInstanceOf[FakeDDMandateAdapter].loadTestData();
    val contractType = "DDMANDATE"
    val ddMandateUUIDString = "79abadf2-84db-42bc-81d5-4577778d38af"
    val ddMandateUUIDReference: UUID = UUID.fromString(ddMandateUUIDString)

    whenReady(contractAdapter.createContract(contractType,ddMandateUUIDReference)){ contract =>

      assert(contract.isInstanceOf[ContractUnSigned])
      assert(contract.reference == ddMandateUUIDString)

      UUIDRegistryHelper.add("adapterContract",contract.identity.uuid,"unsigned")

    }
  }

  test("get contract"){

    val contractUUID: UUID = UUIDRegistryHelper.search("adapterContract","unsigned").get

    whenReady(contractAdapter.findByIdContract(contractUUID)){ contract =>

      assert(contract.identity.uuid == contractUUID)

    }
  }

  private def debug(message: ErrorMessage){
    println(message.errorCode)
    println(message.exceptionType)
    println(message.instance)
    println(message.message)
  }

}
