package com.abaddon83.legal.tests.utilities

import java.util.{Date, UUID}

import com.abaddon83.legal.domainModel.contract.FileRepositories.FileRepository
import com.abaddon83.legal.domainModel.contract.{Contract, ContractSigned, ContractUnSigned}
import com.abaddon83.legal.domainModel.ddMandates.{Creditor, DDMandate, DDMandateAccepted, DDMandateDraft, DDMandateNotAccepted, Debtor, IT1}
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.{BankAccount, BankAccountIdentity, EUBankAccount, UKBankAccount}
import com.abaddon83.shared.DateUtils

trait DomainElementHelper {
  protected def buildDraftDDMandate(bankAccount: BankAccount): DDMandateDraft ={
    val debtor = buildDebtor(bankAccount)
    val creditor = buildCreditor()
    DDMandateDraft(debtor,creditor)
  }

  protected def buildNotAcceptedDDMandate(bankAccount: BankAccount,isContractSigned: Boolean): DDMandateNotAccepted = {
    val ddMandateDraft = buildDraftDDMandate(bankAccount)
    val contract = buildContract(ddMandateDraft,isContractSigned)
    ddMandateDraft.assignContract(contract)
  }

  protected def buildAcceptedDDMandate : DDMandateAccepted = {
    val ddMandateNotAccepted = buildNotAcceptedDDMandate(buildEUBankAccount(true),isContractSigned = true)
    ddMandateNotAccepted.accept()
  }

  protected def buildContract(ddMandate:DDMandate, isSigned: Boolean):Contract={
    isSigned match {
      case true => ContractUnSigned(ddMandate,Some(fakeFileRepository)).sign(fakeFileRepository,new Date)
      case false => ContractUnSigned(ddMandate,Some(fakeFileRepository))
    }
  }

  protected def buildUnsignedContract(): ContractUnSigned = {
    ContractUnSigned(buildDraftDDMandate(buildEUBankAccount(false)),Some(fakeFileRepository))
  }

  protected def buildSignedContract(): ContractSigned = {
    ContractUnSigned(buildDraftDDMandate(buildEUBankAccount(false)),Some(fakeFileRepository)).sign(fakeFileRepository, new Date())
  }

  protected def buildDebtor(bankAccount: BankAccount):Debtor = {
    val firstName = "First"
    val userId = 12345
    val lastName = "Last"
    val taxcode = "TAXCODE"
    val birthDate = DateUtils.buildDateFromString("2000-01-01",None)

    Debtor(userId,firstName,lastName,taxcode,birthDate,bankAccount)
  }

  protected def buildCreditor(): Creditor = {
    Creditor( IT1,"Italian Company 1",buildEUBankAccount(true),"123456")
  }

  protected def buildEUBankAccount(validated: Boolean): BankAccount = {
    val id = BankAccountIdentity(UUID.fromString("b305ef5f-271b-49be-b12f-8f1ed47c4199"))
    buildEUBankAccount(validated,id)
  }

  protected def buildEUBankAccount(validated: Boolean, bankAccountId: BankAccountIdentity): BankAccount = {
    buildBankAccount(validated,bankAccountId,"EU")
  }

  protected def buildUKBankAccount(validated: Boolean): BankAccount = {
    val id = BankAccountIdentity(UUID.fromString("a462886a-2945-4dfc-bdf0-26d60fbf5868"))
    buildUKBankAccount(validated,id)
  }

  protected def buildUKBankAccount(validated: Boolean,bankAccountId: BankAccountIdentity): BankAccount = {
    buildBankAccount(validated,bankAccountId,"UK")
  }

  protected def buildBankAccount(validated: Boolean, bankAccountId: BankAccountIdentity, country: String): BankAccount = {
    country match {
      case "EU" => EUBankAccount(bankAccountId,iban = "IT123456",validated)
      case "UK" => UKBankAccount(bankAccountId,sortCode = 123456, accountNumber = 12345678,validated)
    }
  }

  protected object fakeFileRepository extends FileRepository {
    override val provider: String = "Fake"

    override def url: String = "ddmandate.contract.test"
  }
}
