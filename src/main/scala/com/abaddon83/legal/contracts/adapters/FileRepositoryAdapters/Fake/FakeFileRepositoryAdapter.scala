package com.abaddon83.legal.contracts.adapters.FileRepositoryAdapters.Fake

import com.abaddon83.legal.contracts.domainModels.DDMandate
import com.abaddon83.legal.contracts.domainModels.FileRepositories.FileRepository
import com.abaddon83.legal.contracts.ports.FileRepositoryPort


class FakeFileRepositoryAdapter extends FileRepositoryPort{
  override def createUnsignedDDMandate(ddMandate: DDMandate): Option[FileRepository] = {
    Some(fakeFileRepository)
  }

  private object fakeFileRepository extends FileRepository {
    override val provider: String = "Fake"

    override def url: String = "ddmandate.contract.test"
  }


}
