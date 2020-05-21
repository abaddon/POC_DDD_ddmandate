package com.abaddon83.legal.contracts.adapters.documentAdapters.fake

import com.abaddon83.legal.contracts.domainModels.DDMandate
import com.abaddon83.legal.contracts.domainModels.FileRepositories.FileRepository
import com.abaddon83.legal.contracts.ports.DocumentPort
import com.abaddon83.legal.sharedValueObjects.contracts.Format

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class FakeDocumentAdapter extends DocumentPort{
  override def createDocument(ddMandate: DDMandate, format: Format): Future[FileRepository] = {
    Future(fakeFileRepository)
  }

  private object fakeFileRepository extends FileRepository {
    override val provider: String = "Fake"

    override def url: String = "ddmandate.contract.test"
  }


}
