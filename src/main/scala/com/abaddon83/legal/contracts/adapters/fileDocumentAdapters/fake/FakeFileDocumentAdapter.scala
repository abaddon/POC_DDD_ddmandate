package com.abaddon83.legal.contracts.adapters.fileDocumentAdapters.fake

import com.abaddon83.legal.contracts.domainModels.DDMandate
import com.abaddon83.legal.contracts.domainModels.FileRepositories.FileRepository
import com.abaddon83.legal.contracts.ports.FileDocumentPort
import com.abaddon83.legal.sharedValueObjects.contracts.Format

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class FakeFileDocumentAdapter extends FileDocumentPort{
  override def createDocument(ddMandate: DDMandate, format: Format): Future[FileRepository] = {
    Future(fakeFileRepository)
  }

  private object fakeFileRepository extends FileRepository {
    override val provider: String = "Fake"

    override def url: String = "ddmandate.contract.test"
  }


}
