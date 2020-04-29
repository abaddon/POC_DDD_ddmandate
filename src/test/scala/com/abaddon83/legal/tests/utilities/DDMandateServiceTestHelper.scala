package com.abaddon83.legal.tests.utilities

import java.util.{Date, UUID}

import com.abaddon83.legal.domainModel.adapters.{FakeBankAccountAdapter, FakeCreditorAdapter, FakeDDMandateRepositoryAdapter}
import com.abaddon83.legal.domainModel.contract.{Contract, ContractUnSigned}
import com.abaddon83.legal.domainModel.ddMandates.{DDMandateDraft, DDMandateNotAccepted, IT1}
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.{BankAccount, BankAccountIdentity}
import com.abaddon83.legal.ports.{BankAccountPort, CreditorPort, DDMandateRepositoryPort}
import com.abaddon83.legal.services.DDMandateService

trait DDMandateServiceTestHelper{

  protected val ddMandateRepository: DDMandateRepositoryPort
  protected val bankAccountPort: BankAccountPort
  protected val creditorPort: CreditorPort
  protected val ddMandateService: DDMandateService

  protected def buildDraftDDMandate(bankAccountId: BankAccountIdentity): DDMandateDraft ={
    val legalEntity = IT1

    ddMandateService.newDraftDDMandate(bankAccountId,legalEntity)
  }

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
