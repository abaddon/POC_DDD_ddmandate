package com.abaddon83.legal.contracts.utilities

import java.util.Date

import com.abaddon83.legal.contracts.domainModels.FileRepositories.FileRepository
import com.abaddon83.legal.contracts.domainModels.{Contract, ContractSigned, ContractUnSigned, DDMandate}
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity


trait ContractDomainElementHelper {

  protected def buildDDMandate(): DDMandate ={
    DDMandate(DDMandateIdentity())
  }

  protected def buildUnsignedContract(): ContractUnSigned = {
    buildContract(buildDDMandate(),false).asInstanceOf[ContractUnSigned]
  }

  protected def buildSignedContract(): ContractSigned = {
    buildContract(buildDDMandate(),true).asInstanceOf[ContractSigned]
  }

  protected def buildContract(ddMandate:DDMandate, isSigned: Boolean): Contract = {
    isSigned match {
      case true => ContractUnSigned(ddMandate,fakeFileRepository).sign(fakeFileRepository,new Date)
      case false => ContractUnSigned(ddMandate,fakeFileRepository)
    }
  }

  protected object fakeFileRepository extends FileRepository {
    override val provider: String = "Fake"

    override def url: String = "ddmandate.contract.test"
  }
}

