package com.abaddon83.legal.contracts.services
/*
import java.util.{Date, UUID}

import com.abaddon83.legal.contracts.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.FileRepositoryAdapters.Fake.FakeFileRepositoryAdapter
import com.abaddon83.legal.contracts.domainModels.{ContractSigned, ContractUnSigned}
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, FileRepositoryPort}
import com.abaddon83.legal.contracts.utilities.{ContractDomainElementHelper, ContractServiceTestHelper}
import com.abaddon83.legal.sharedValueObjects.contracts.{ContractIdentity, DD_MANDATE}
import org.scalatest.funsuite.AnyFunSuite

class ContractServiceTest extends AnyFunSuite with  ContractServiceTestHelper with ContractDomainElementHelper {

  override protected val contractRepository: ContractRepositoryPort = new FakeContractRepositoryAdapter
  override protected val fileRepository: FileRepositoryPort = new FakeFileRepositoryAdapter
  override protected val contractService: ContractService = new ContractService(contractRepository,fileRepository)

  test("create a DD Mandate contract unsigned"){

    val ddMandate = buildDraftDDMandate(buildEUBankAccount(false))
    val contract = contractService.createDDMandateContract(ddMandate)

    //println(s"contract.identity.uuid: ${contract.identity.uuid}")

    val contractPersisted = contractRepository.findByContractUnSignedByIdentity(contract.identity)

    //println(s"contractPersisted ${contractPersisted.isDefined} ${contractPersisted.map(f=> f.identity.uuid)}")

    assert(contractPersisted.get.contractType == DD_MANDATE)
    assert(contractPersisted.get.reference == ddMandate.identity.uuid.toString)
  }

  test("sign a contract unsigned"){

    val contractIdentity = ContractIdentity(UUID.fromString("1469e8b0-7b98-4755-96b4-c3efea1a5894"))
    val signatureDate = new Date()
    val contractUnsigned = contractRepository.findByContractUnSignedByIdentity(contractIdentity)
    assert(contractUnsigned.isInstanceOf[Option[ContractUnSigned]])

    contractService.signContract(contractIdentity,fakeFileRepository,signatureDate)

    val contractUpdated = contractRepository.findByContractSignedByIdentity(contractIdentity)
    assert(contractUpdated.isInstanceOf[Option[ContractSigned]])
    assert(contractUpdated.get.signatureDate == signatureDate)
  }

  test("sign a contract signed"){
    val contractIdentity = ContractIdentity(UUID.fromString("1469e8b0-7b98-4755-96b4-c3efea1a5894"))
    val signatureDate = new Date()
    assertThrows[IllegalArgumentException] {
      contractService.signContract(contractIdentity,fakeFileRepository,signatureDate)
    }
  }

  test("sign a contract that doesn't exist"){
    val contractIdentity = ContractIdentity(UUID.fromString("3aae4033-274a-4883-bc4f-7be96e0a9e27"))
    val signatureDate = new Date()
    assertThrows[IllegalArgumentException] {
      contractService.signContract(contractIdentity,fakeFileRepository,signatureDate)
    }
  }


}
*/