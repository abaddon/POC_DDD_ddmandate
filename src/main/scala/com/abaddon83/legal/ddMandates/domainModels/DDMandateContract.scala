package com.abaddon83.legal.ddMandates.domainModels

import java.util.Date

import com.abaddon83.legal.shares.contracts.{ContractIdentity, ContractType, Format}

case class DDMandateContract(
                             identity: ContractIdentity,
                             reference: String,
                             contractType:ContractType,
                             name: String,
                             format: Format,
                             //file: FileRepository,
                             creationDate: Date,
                             //signedFile: FileRepository,
                             signatureDate: Option[Date]
                           ) {

  def isSigned: Boolean = {
    signatureDate.isDefined
  }
}

