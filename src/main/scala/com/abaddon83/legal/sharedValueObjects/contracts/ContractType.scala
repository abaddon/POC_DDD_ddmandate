package com.abaddon83.legal.sharedValueObjects.contracts

sealed trait ContractType{
    def name(descriptions: Option[String]): String
    def format: Format
    def toString(): String
}

case object DD_MANDATE extends ContractType {
    override def name(description: Option[String]): String = {
      "Direct Debit Mandate"++description.getOrElse("")
    }
    override def format: Format = PDF
    override def toString() = "DDMANDATE"
  }

case object TC_AGREEMENT extends ContractType{
    override def name(description: Option[String]): String = {
      "T&C Agreement"++description.getOrElse("")
    }
    override def format: Format = PDF
    override def toString() = "T&C"
  }

