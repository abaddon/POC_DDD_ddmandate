package com.abaddon83.legal.domainModel.adapters


import com.abaddon83.legal.domainModel.contract.FileRepositories.{FileRepository, S3FileRepository}
import com.abaddon83.legal.domainModel.ddMandates.DDMandate
import com.abaddon83.legal.ports.FileRepositoryPort

import scala.collection.mutable.ListBuffer

class FakeFileRepositoryAdapter extends FileRepositoryPort{
  override def createUnsignedDDMandate(ddMandate: DDMandate): Option[FileRepository] = {
    Some(fakeFileRepository)
  }

  private object fakeFileRepository extends FileRepository {
    override val provider: String = "Fake"

    override def url: String = "ddmandate.contract.test"
  }


}
