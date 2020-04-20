package com.abaddon83.legal.domainModel.contract


sealed trait ContractType{
    def name(descriptions: Option[String]): String
    def format: Format
}

case object DD_MANDATE extends ContractType {
    override def name(description: Option[String]): String = {
      "Direct Debit Mandate"++description.getOrElse("")
    }
    override def format: Format = PDF
  }

case object PIP_AGREEMENT extends ContractType{
    override def name(description: Option[String]): String = {
      "PIP Agreement"++description.getOrElse("")
    }
    override def format: Format = PDF
  }

case object SIPP_AGREEMENT extends ContractType{
    override def name(description: Option[String]): String = {
      "SIPP Agreement"++description.getOrElse("")
    }
    override def format: Format = PDF
  }

case object GIA_AGREEMENT extends ContractType{
    override def name(description: Option[String]): String = {
      "GIA Agreement"++description.getOrElse("")
    }
    override def format: Format = PDF
  }

case object ISA_AGREEMENT extends ContractType{
    override def name(description: Option[String]): String = {
      "ISA Agreement"++description.getOrElse("")
    }
    override def format: Format = PDF
  }

