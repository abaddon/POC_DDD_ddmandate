package com.abaddon83.legal.domainModel.ddMandates

import java.util.Date

import com.abaddon83.legal.domainModel.contract.{Contract, ContractSigned}
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.BankAccount

sealed trait DDMandate{
  val identity: DDMandateIdentity
  val ddMandateType: DDMandateType
  val debtor: Debtor
  val creditor: Creditor
  val creationDate: Date
  val status: Status
}
//DD MANDATE DRAFT
case class DDMandateDraft(
        identity: DDMandateIdentity,
        ddMandateType: DDMandateType,
        debtor: Debtor,
        creditor: Creditor,
        creationDate: Date,
        status: Status = DRAFT) extends DDMandate {

  def assignContract(contract:Contract): DDMandateNotAccepted = {
    assert(status == DRAFT, "Contract can be assigned only to a DRAFT mandate")
    assert(identity.toString == contract.reference,"The reference on the Contract doesn't match mandate identifier ")

    DDMandateNotAccepted(this,contract)
  }
}

object DDMandateDraft{
  def apply(debtor: Debtor, creditor: Creditor): DDMandateDraft = {
    val ddMandateDraft = new DDMandateDraft(DDMandateIdentity(),Financial,debtor,creditor,new Date())
    assert(ddMandateDraft.ddMandateType == Financial,"The DD mandate has to be Financial")
    assert(ddMandateDraft.status == DRAFT ,"The DD mandate has to be DRAFT")
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
                           contract: Contract,
                           status: Status = NOACCEPTED) extends DDMandate {


  def accept(): DDMandateAccepted = {
    assert(this.debtor.bankAccount.isValid,"The bank account has to be valid to accept a DD Mandate")
    assert(this.status == NOACCEPTED, "The DD Mandate status has to be not accepted")
    assert(this.contract.isSigned, "The contract has to be signed")

    DDMandateAccepted(this)
  }
}

object DDMandateNotAccepted{
  def apply(ddMandateDraft: DDMandateDraft, contract: Contract): DDMandateNotAccepted = {
    val ddMandateNotAccepted = new DDMandateNotAccepted(ddMandateDraft.identity,ddMandateDraft.ddMandateType,ddMandateDraft.debtor,ddMandateDraft.creditor,ddMandateDraft.creationDate,contract)
    assert(ddMandateNotAccepted.identity == ddMandateDraft.identity)
    assert(ddMandateNotAccepted.ddMandateType == ddMandateDraft.ddMandateType)
    assert(ddMandateNotAccepted.creditor == ddMandateDraft.creditor)
    assert(ddMandateNotAccepted.debtor == ddMandateDraft.debtor)
    assert(ddMandateNotAccepted.creationDate == ddMandateDraft.creationDate)
    assert(ddMandateNotAccepted.status == NOACCEPTED)
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
                              contract: Contract,
                              status: Status = ACCEPTED) extends DDMandate {

  def cancel(): DDMandateCanceled = {
    assert(status == ACCEPTED, "The DD Mandate status has to be accepted")

    DDMandateCanceled(this)
  }
}

object DDMandateAccepted{
  def apply(ddMandateNotAccepted: DDMandateNotAccepted): DDMandateAccepted = {

    val ddMandateAccepted = new DDMandateAccepted(ddMandateNotAccepted.identity,ddMandateNotAccepted.ddMandateType,ddMandateNotAccepted.debtor,ddMandateNotAccepted.creditor,ddMandateNotAccepted.creationDate,ddMandateNotAccepted.contract)

    assert(ddMandateAccepted.identity == ddMandateNotAccepted.identity)
    assert(ddMandateAccepted.ddMandateType == ddMandateNotAccepted.ddMandateType)
    assert(ddMandateAccepted.creditor == ddMandateNotAccepted.creditor)
    assert(ddMandateAccepted.debtor == ddMandateNotAccepted.debtor)
    assert(ddMandateAccepted.creationDate == ddMandateNotAccepted.creationDate)
    assert(ddMandateAccepted.status == ACCEPTED)
    assert(ddMandateAccepted.contract == ddMandateNotAccepted.contract)
    assert(ddMandateAccepted.contract.isSigned)

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
                              cancellationDate: Date,
                              status: Status = CANCELED) extends DDMandate {

}

object DDMandateCanceled{
  def apply(ddMandateAccepted: DDMandateAccepted): DDMandateCanceled = {
    val ddMandateCanceled = new DDMandateCanceled(ddMandateAccepted.identity,ddMandateAccepted.ddMandateType,ddMandateAccepted.debtor,ddMandateAccepted.creditor,ddMandateAccepted.creationDate,ddMandateAccepted.contract,new Date())
    assert(ddMandateCanceled.identity == ddMandateAccepted.identity)
    assert(ddMandateCanceled.ddMandateType == ddMandateAccepted.ddMandateType)
    assert(ddMandateCanceled.creditor == ddMandateAccepted.creditor)
    assert(ddMandateCanceled.debtor == ddMandateAccepted.debtor)
    assert(ddMandateCanceled.creationDate == ddMandateAccepted.creationDate)
    assert(ddMandateCanceled.status == CANCELED)
    assert(ddMandateCanceled.contract == ddMandateAccepted.contract)
    ddMandateCanceled
  }
}











