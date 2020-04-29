package com.abaddon83.legal.tests.utilities

import com.abaddon83.legal.ports.{ContractRepositoryPort, FileRepositoryPort}
import com.abaddon83.legal.services.ContractService

trait ContractServiceTestHelper{

  protected val contractRepository: ContractRepositoryPort
  protected val fileRepository: FileRepositoryPort
  protected val contractService: ContractService



  /*protected def buildDDMandateNotAccepted(bankAccountId: BankAccountIdentity, contractSigned: Boolean): DDMandateNotAccepted = {
    val ddMandateDraft = buildDraftDDMandate(bankAccountId)
    val unsignedContract = isSigned match {
      case true => ContractUnSigned(ddMandateDraft,Some(fakeFileRepository)).sign(fakeFileRepository,new Date)
      case false => ContractUnSigned(ddMandateDraft,Some(fakeFileRepository))
    }
    ddMandateService.createDDMandate(ddMandateDraft,unsignedContract)
  }
*/
  }
