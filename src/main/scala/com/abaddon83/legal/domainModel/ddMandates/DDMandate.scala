package com.abaddon83.legal.domainModel.ddMandates

import java.util.Date

import com.abaddon83.legal.domainModel.contract.{Contract, ContractSigned}
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.BankAccount
import com.abaddon83.shared.ddd.Entity

sealed trait DDMandate extends Entity {

  val identity: DDMandateIdentity
  val ddMandateType: DDMandateType
  val debtor: Debtor
  val creditor: Creditor
  val creationDate: Date
}
//DD MANDATE DRAFT
case class DDMandateDraft(
        identity: DDMandateIdentity,
        ddMandateType: DDMandateType,
        debtor: Debtor,
        creditor: Creditor,
        creationDate: Date) extends DDMandate {


  def assignContract(contract:Contract): DDMandateNotAccepted = {

    assertArgumentEquals(identity.uuid.toString,contract.reference,"The reference on the Contract doesn't match mandate identifier")

    DDMandateNotAccepted(this,contract)
  }
}

object DDMandateDraft extends Entity{
  def apply(debtor: Debtor, creditor: Creditor): DDMandateDraft = {

    val ddMandateDraft = new DDMandateDraft(DDMandateIdentity(),Financial,debtor,creditor,new Date())

    //PRO
    assert(ddMandateDraft.ddMandateType == Financial,"The DD mandate has to be Financial")

    ddMandateDraft
  }
}

//DD MANDATE NOT ACCEPTED

case class DDMandateNotAccepted(
                           identity: DDMandateIdentity,
                           ddMandateType: DDMandateType,
                           debtor: Debtor,
                           creditor: Creditor,
                           creationDate: Date,
                           contract: Contract) extends DDMandate {

  def updateContractSigned(contractSigned: ContractSigned): DDMandateNotAccepted ={

    assert(contractSigned.identity == contract.identity, "The contract identifier is wrong")
    this.copy(contract = contractSigned)
  }

  def updateDebtorValidated(debtorValidated: Debtor): DDMandateNotAccepted ={
    assert(debtorValidated.bankAccount.isValid, "BankAccount is not valid")
    assert(debtorValidated.bankAccount.identity == debtor.bankAccount.identity,"The bankAccount identifier is wrong")
    this.copy(debtor = debtorValidated)
  }

  def accept(): DDMandateAccepted = {
    assert(this.debtor.bankAccount.isValid,"The bank account has to be valid to accept a DD Mandate")
    assert(this.contract.isInstanceOf[ContractSigned], "The contract has to be signed")

    DDMandateAccepted(this)
  }
}

object DDMandateNotAccepted extends Entity{
  def apply(ddMandateDraft: DDMandateDraft, contract: Contract): DDMandateNotAccepted = {
    //PRE

    val ddMandateNotAccepted = new DDMandateNotAccepted(ddMandateDraft.identity,ddMandateDraft.ddMandateType,ddMandateDraft.debtor,ddMandateDraft.creditor,ddMandateDraft.creationDate,contract)
    assert(ddMandateNotAccepted.identity == ddMandateDraft.identity)
    assert(ddMandateNotAccepted.ddMandateType == ddMandateDraft.ddMandateType)
    assert(ddMandateNotAccepted.creditor == ddMandateDraft.creditor)
    assert(ddMandateNotAccepted.debtor == ddMandateDraft.debtor)
    assert(ddMandateNotAccepted.creationDate == ddMandateDraft.creationDate)
    assert(ddMandateNotAccepted.contract == contract)
    ddMandateNotAccepted
  }
}

//DD MANDATE ACCEPTED

case class DDMandateAccepted(
                              identity: DDMandateIdentity,
                              ddMandateType: DDMandateType,
                              debtor: Debtor,
                              creditor: Creditor,
                              creationDate: Date,
                              contract: Contract) extends DDMandate {

  def cancel(): DDMandateCanceled = {

    DDMandateCanceled(this)
  }
}

object DDMandateAccepted extends Entity{
  def apply(ddMandateNotAccepted: DDMandateNotAccepted): DDMandateAccepted = {

    val ddMandateAccepted = new DDMandateAccepted(ddMandateNotAccepted.identity,ddMandateNotAccepted.ddMandateType,ddMandateNotAccepted.debtor,ddMandateNotAccepted.creditor,ddMandateNotAccepted.creationDate,ddMandateNotAccepted.contract)

    assert(ddMandateAccepted.identity == ddMandateNotAccepted.identity)
    assert(ddMandateAccepted.ddMandateType == ddMandateNotAccepted.ddMandateType)
    assert(ddMandateAccepted.creditor == ddMandateNotAccepted.creditor)
    assert(ddMandateAccepted.debtor == ddMandateNotAccepted.debtor)
    assert(ddMandateAccepted.creationDate == ddMandateNotAccepted.creationDate)
    assert(ddMandateAccepted.contract == ddMandateNotAccepted.contract)
    assert(ddMandateAccepted.contract.isInstanceOf[ContractSigned])

    ddMandateAccepted
  }
}

case class DDMandateCanceled(
                              identity: DDMandateIdentity,
                              ddMandateType: DDMandateType,
                              debtor: Debtor,
                              creditor: Creditor,
                              creationDate: Date,
                              contract: Contract,
                              cancellationDate: Date) extends DDMandate {

}

object DDMandateCanceled extends Entity{
  def apply(ddMandateAccepted: DDMandateAccepted): DDMandateCanceled = {
    val ddMandateCanceled = new DDMandateCanceled(ddMandateAccepted.identity,ddMandateAccepted.ddMandateType,ddMandateAccepted.debtor,ddMandateAccepted.creditor,ddMandateAccepted.creationDate,ddMandateAccepted.contract,new Date())
    assert(ddMandateCanceled.identity == ddMandateAccepted.identity)
    assert(ddMandateCanceled.ddMandateType == ddMandateAccepted.ddMandateType)
    assert(ddMandateCanceled.creditor == ddMandateAccepted.creditor)
    assert(ddMandateCanceled.debtor == ddMandateAccepted.debtor)
    assert(ddMandateCanceled.creationDate == ddMandateAccepted.creationDate)
    assert(ddMandateCanceled.contract == ddMandateAccepted.contract)
    ddMandateCanceled
  }
}











