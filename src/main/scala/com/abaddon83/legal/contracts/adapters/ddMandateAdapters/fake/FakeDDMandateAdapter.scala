package com.abaddon83.legal.contracts.adapters.ddMandateAdapters.fake

import java.util.UUID

import com.abaddon83.legal.contracts.domainModels.DDMandate
import com.abaddon83.legal.contracts.ports.DDMandatePort
import com.abaddon83.legal.sharedValueObjects.ddMandates.DDMandateIdentity
import com.abaddon83.libs.InMemoryRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class FakeDDMandateAdapter extends DDMandatePort with InMemoryRepository[DDMandate]{

  override def findDDMandateById(ddMandateIdentity: DDMandateIdentity): Future[DDMandate] = {
    Future{
      repository.db.find(ddMandate => ddMandate.identity == ddMandateIdentity)
        .getOrElse(throw new NoSuchElementException(s"DDMandate with id: ${ddMandateIdentity} not found"))
    }
  }

  def loadTestData(): Unit ={
    persist(DDMandate(DDMandateIdentity(UUID.fromString("79abadf2-84db-42bc-81d5-4577778d38af"))))
    persist(DDMandate(DDMandateIdentity(UUID.fromString("ab233acf-d39a-4267-b17b-c462d26e5680"))))
  }


  def addTestableDDMandate(ddMandate: DDMandate): Unit ={
    persist(ddMandate)
  }

}
