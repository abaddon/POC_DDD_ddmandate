package com.abaddon83.legal.contracts.services

import java.util.{Date, UUID}

import com.abaddon83.legal.contracts.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.ddMandateAdapters.fake.FakeDDMandateAdapter
import com.abaddon83.legal.contracts.adapters.fileDocumentAdapters.fake.FakeFileDocumentAdapter
import com.abaddon83.legal.contracts.domainModels.{ContractUnSigned, DDMandate}
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, DDMandatePort, FileDocumentPort}
import com.abaddon83.legal.contracts.utilities.ContractDomainElementHelper
import com.abaddon83.legal.sharedValueObjects.contracts.{ContractIdentity, DD_MANDATE}
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity
import com.abaddon83.legal.utilities.UUIDRegistryHelper
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite

class ContractServiceTest extends AnyFunSuite with ScalaFutures with ContractDomainElementHelper {

  val contractRepository: ContractRepositoryPort = new FakeContractRepositoryAdapter
  val fileRepository: FileDocumentPort = new FakeFileDocumentAdapter
  val ddMandatePort: DDMandatePort = new FakeDDMandateAdapter
  val contractService: ContractService = new ContractService(contractRepository,fileRepository,ddMandatePort)


  test("create a DD Mandate contract unsigned"){

    val ddMandate = DDMandate(DDMandateIdentity())
    ddMandatePort.asInstanceOf[FakeDDMandateAdapter].addTestableDDMandate(ddMandate)

    val contract = contractService.createDDMandateContract(ddMandate.identity).futureValue

    val contractPersisted = contractRepository.findContractUnSignedByIdentity(contract.identity).futureValue

    assert(contractPersisted.contractType == DD_MANDATE)
    assert(contractPersisted.reference == ddMandate.identity.convertTo().toString)
    UUIDRegistryHelper.add("service_contract",contract.identity.convertTo(),"unsigned")
  }

  test("sign a contract unsigned"){

    val contractIdentity = ContractIdentity(UUIDRegistryHelper.search("service_contract","unsigned").get)
    val signatureDate = new Date()
    val contractUnsigned = contractRepository.findContractUnSignedByIdentity(contractIdentity).futureValue
    assert(contractUnsigned.isInstanceOf[ContractUnSigned])

    contractService.signContract(contractIdentity,fakeFileRepository,signatureDate).futureValue

    val contractUpdated = contractRepository.findContractSignedByIdentity(contractIdentity).futureValue
    assert(contractUpdated.signatureDate == signatureDate)

    UUIDRegistryHelper.update("service_contract",contractUpdated.identity.convertTo(),"signed")
  }

  test("sign a contract signed"){
    val contractIdentity = ContractIdentity(UUIDRegistryHelper.search("service_contract","signed").get)
    val signatureDate = new Date()

    assert(contractService.signContract(contractIdentity,fakeFileRepository,signatureDate).failed.futureValue.isInstanceOf[NoSuchElementException])
  }

  test("sign a contract that doesn't exist"){
    val contractIdentity = ContractIdentity(UUID.fromString("3aae4033-274a-4883-bc4f-7be96e0a9e27"))
    val signatureDate = new Date()

    assert(contractService.signContract(contractIdentity,fakeFileRepository,signatureDate).failed.futureValue.isInstanceOf[NoSuchElementException])
  }


}
