package com.abaddon83.legal.adapters.FileRepositoryAdapters

import com.abaddon83.legal.domainModel.contract.FileRepositories.FileRepository
import com.abaddon83.legal.domainModel.ddMandates.DDMandate
import com.abaddon83.legal.ports.FileRepositoryPort

class FakeFileRepositoryAdapter extends FileRepositoryPort{
  override def createUnsignedDDMandate(ddMandate: DDMandate): Option[FileRepository] = {
    Some(fakeFileRepository)
  }

  private object fakeFileRepository extends FileRepository {
    override val provider: String = "Fake"

    override def url: String = "ddmandate.contract.test"
  }


}
