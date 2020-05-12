package com.abaddon83.legal.contracts

import com.abaddon83.legal.contracts.adapters.ContractRepositoryAdapters.Fake.FakeContractRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.FileRepositoryAdapters.Fake.FakeFileRepositoryAdapter
import com.abaddon83.legal.contracts.adapters.ddMandateAdapters.fake.FakeDDMandateAdapter
import com.abaddon83.legal.contracts.ports.{ContractRepositoryPort, DDMandatePort, FileRepositoryPort}
import wvlet.airframe._

trait ContractBindedAdapters {

  val contractBindDesign = newDesign
    .bind[DDMandatePort].toInstance(new FakeDDMandateAdapter)
    .bind[ContractRepositoryPort].toInstance(new FakeContractRepositoryAdapter)
    .bind[FileRepositoryPort].toInstance(new FakeFileRepositoryAdapter)
}
