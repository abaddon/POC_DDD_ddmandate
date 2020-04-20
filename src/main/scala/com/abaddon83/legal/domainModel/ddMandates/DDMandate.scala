package com.abaddon83.legal.domainModel.ddMandates

import java.util.Date

import com.abaddon83.legal.domainModel.contract.Contract
import com.abaddon83.legal.domainModel.ddMandates.bankAccount.BankAccount

object DDMandate{
  def apply(debtor: Debtor, creditor: Creditor) = new DDMandate(DDMandateIdentity(),Financial,debtor,creditor,new Date(),DRAFT,None)
}

case class DDMandate (
      identity: DDMandateIdentity,
      ddMandateType: DDMandateType,
      debtor: Debtor,
      creditor: Creditor,
      creationDate: Date,
      var status: Status,
      var contract: Option[Contract]
  ){

  def assignContract(contract:Contract): DDMandate = {
    assert(this.contract.isEmpty,"Contract has to be empty")
    assert(status == DRAFT, "Contract can be assigned only to a DRAFT mandate")
    assert(identity.toString == contract.reference,"The reference on the Contract doesn't match mandate identifier ")
    this.contract = Option(contract)
    this.status = NOACCEPTED
    assert(this.status == NOACCEPTED)
    assert(this.contract.isDefined)
    this
  }

  def accept(debtorBankAccount: BankAccount ): DDMandate = {
    assert(this.contract.get.isSigned(),"Contract has to be signed before accept the DD mandate")
    assert(debtorBankAccount.isValid,"The bank account has to be valid to accept a DD Mandate")
    assert(this.status == NOACCEPTED, "The DD Mandate status has to be not accepted")

    debtor.bankAccount.validate()
    status = ACCEPTED

    assert(this.status == ACCEPTED)
    this
  }

  def cancel(): DDMandate = {
    assert(this.status == ACCEPTED, "The DD Mandate status has to be accepted")
    this.status = CANCELED
    assert(this.status == CANCELED, "The DD Mandate status has to be CANCELED")
    this
  }

}







