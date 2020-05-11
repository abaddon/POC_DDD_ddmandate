package com.abaddon83.legal.ddMandates.domainModels

import java.util.Date

import com.abaddon83.legal.sharedValueObjects.contracts.{ContractIdentity, ContractType, Format}


sealed trait Contract{
  val identity: ContractIdentity
  val reference: String
  val contractType: ContractType
}

case class ContractUnSigned(
                             identity: ContractIdentity,
                             reference: String,
                             contractType:ContractType,
                             name: String,
                             format: Format,
                             //file: FileRepository,
                             creationDate: Date
                           ) extends Contract

case class ContractSigned(
                             identity: ContractIdentity,
                             reference: String,
                             contractType:ContractType,
                             name: String,
                             format: Format,
                             //file: FileRepository,
                             creationDate: Date,
                             //signedFile: FileRepository,
                             signatureDate: Date
                           ) extends Contract

