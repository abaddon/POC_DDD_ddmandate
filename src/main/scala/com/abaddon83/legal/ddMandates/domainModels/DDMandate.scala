package com.abaddon83.legal.ddMandates.domainModels

import java.util.Date

import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity
import com.abaddon83.libs.ddd.Entity

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


  def assignContract(contract:DDMandateContract): DDMandateNotAccepted = {

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
                           contract: DDMandateContract) extends DDMandate {

  def updateContractSigned(contractSigned: DDMandateContract): DDMandateNotAccepted ={

    assert(contractSigned.identity == contract.identity, "The contract identifier is wrong")
    this.copy(contract = contractSigned)
  }

  /*def updateDebtorValidated(debtorValidated: Debtor): DDMandateNotAccepted ={
    assert(debtorValidated.bankAccount.isValid, "BankAccount is not valid")
    assert(debtorValidated.bankAccount.identity == debtor.bankAccount.identity,"The bankAccount identifier is wrong")
    this.copy(debtor = debtorValidated)
  }*/

  def accept(contractSigned: DDMandateContract, debtorValidated: Debtor): DDMandateAccepted = {

    DDMandateAccepted(this, contractSigned, debtorValidated)
  }
}

object DDMandateNotAccepted extends Entity{
  def apply(ddMandateDraft: DDMandateDraft, contract: DDMandateContract): DDMandateNotAccepted = {
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
                              contract: DDMandateContract) extends DDMandate {

  def cancel(): DDMandateCanceled = {

    DDMandateCanceled(this)
  }
}

object DDMandateAccepted extends Entity{
  def apply(ddMandateNotAccepted: DDMandateNotAccepted, contractSigned: DDMandateContract, debtorValidated: Debtor): DDMandateAccepted = {

    //PRE
    assert(debtorValidated.bankAccount.isValid, "The bank account has to be valid to accept a DD Mandate")
    assert(debtorValidated.bankAccount.identity == ddMandateNotAccepted.debtor.bankAccount.identity, s"The valid debtor bankAccount is not the same, bank account expected: ${ddMandateNotAccepted.debtor.bankAccount.identity}")
    assert(contractSigned.identity == ddMandateNotAccepted.contract.identity,s"The contract signed used is not the same, contract expected: ${ddMandateNotAccepted.contract.identity}")
    assert(contractSigned.isSigned ,s"The contract has to be signed")

    val ddMandateAccepted = new DDMandateAccepted(ddMandateNotAccepted.identity,ddMandateNotAccepted.ddMandateType,debtorValidated ,ddMandateNotAccepted.creditor,ddMandateNotAccepted.creationDate,contractSigned)

    //POST
    assert(ddMandateAccepted.identity == ddMandateNotAccepted.identity)
    assert(ddMandateAccepted.ddMandateType == ddMandateNotAccepted.ddMandateType)
    assert(ddMandateAccepted.creditor == ddMandateNotAccepted.creditor)
    assert(ddMandateAccepted.debtor == debtorValidated)
    assert(ddMandateAccepted.creationDate == ddMandateNotAccepted.creationDate)
    ddMandateAccepted
  }
}

case class DDMandateCanceled(
                              identity: DDMandateIdentity,
                              ddMandateType: DDMandateType,
                              debtor: Debtor,
                              creditor: Creditor,
                              creationDate: Date,
                              contract: DDMandateContract,
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











