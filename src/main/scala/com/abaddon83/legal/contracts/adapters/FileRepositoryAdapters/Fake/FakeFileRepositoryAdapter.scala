package com.abaddon83.legal.contracts.adapters.FileRepositoryAdapters.Fake

import com.abaddon83.legal.contracts.domainModels.DDMandate
import com.abaddon83.legal.contracts.domainModels.FileRepositories.FileRepository
import com.abaddon83.legal.contracts.ports.FileRepositoryPort

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class FakeFileRepositoryAdapter extends FileRepositoryPort{
  override def createUnsignedDDMandate(ddMandate: DDMandate): Future[FileRepository] = {
    Future(fakeFileRepository)
  }

  private object fakeFileRepository extends FileRepository {
    override val provider: String = "Fake"

    override def url: String = "ddmandate.contract.test"
  }


}
