package com.abaddon83.legal.ddMandates.adapters.ddMandateContractAdapters.internal
/*
class InternalDDMandateContractAdapter extends DDMandateContractPort with ContractAdapter {

  override def findSignedContractByContractId(contractIdentity: ContractIdentity): Option[DDMandateContract] = {
    var ddMandateContract = for {
      contract <- findByIdContract(contractIdentity.uuid)
    } yield convertToDDMandateContract(contract)



  }

  override def createContract(ddMandate: DDMandate): Option[DDMandateContract] = ???


  def convertToDDMandateContract( contact: Contract): DDMandateContract = {
    contact match {
      case contractUnSigned: ContractUnSigned => convertToDDMandateContract(contractUnSigned)
      case contractSigned: ContractSigned => convertToDDMandateContract(contractSigned)
    }

  }

  def convertToDDMandateContract( contract: ContractUnSigned): DDMandateContract = {
    DDMandateContract(contract.identity,contract.reference,contract.contractType,contract.name,contract.format,contract.creationDate,Some(contract.signatureDate))
  }

  def convertToDDMandateContract( contract: ContractSigned): DDMandateContract = {
    DDMandateContract(contract.identity,contract.reference,contract.contractType,contract.name,contract.format,contract.creationDate,Some(contract.signatureDate))
  }
}

 */



