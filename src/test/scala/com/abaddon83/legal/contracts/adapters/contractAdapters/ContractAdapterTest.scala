package com.abaddon83.legal.contracts.adapters.contractAdapters

import java.util.UUID

import com.abaddon83.legal.contracts.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.FileRepositoryAdapters.Fake.FakeFileRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.contractAdapters.akkaHttp.ContractAdapter
import com.abaddon83.legal.contracts.adapters.ddMandateAdapters.fake.FakeDDMandateAdapter
import com.abaddon83.legal.contracts.domainModels.ContractUnSigned
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, DDMandatePort, FileRepositoryPort}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import wvlet.airframe._

class ContractAdapterTest extends AnyFunSuite with Matchers with ScalaFutures {{

  val session = newDesign
    .bind[DDMandatePort].toInstance(new FakeDDMandateAdapter())
    .bind[ContractRepositoryPort].toInstance(new FakeContractRepositoryAdapter() )
    .bind[FileRepositoryPort].toInstance(new FakeFileRepositoryAdapter())
    .newSession

  val contractAdapter = session.build[ContractAdapter]

  test(""){
    contractAdapter.ddMandatePort.asInstanceOf[FakeDDMandateAdapter].loadTestData();
    val contractType = "DDMANDATE"
    val ddMandateUUIDString = "79abadf2-84db-42bc-81d5-4577778d38af"
    val ddMandateUUIDReference: UUID = UUID.fromString(ddMandateUUIDString)

    whenReady(contractAdapter.createContract(contractType,ddMandateUUIDReference)){ contract =>

      assert(contract.isInstanceOf[ContractUnSigned])
      assert(contract.reference == ddMandateUUIDString)

    }
  }
}

}
