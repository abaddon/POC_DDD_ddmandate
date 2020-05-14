package com.abaddon83.legal.ddMandates

import com.abaddon83.legal.ddMandates.adapters.CreditorAdapters.fake.FakeCreditorAdapter
import com.abaddon83.legal.ddMandates.adapters.bankAccountAdapters.fake.FakeBankAccountAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateContractAdapters.fake.FakeDDMandateContractAdapter
import com.abaddon83.legal.ddMandates.adapters.ddMandateRepositoryAdapters.fake.FakeDDMandateRepositoryAdapter
import com.abaddon83.legal.ddMandates.ports.{BankAccountPort, DDMandateContractPort, CreditorPort, DDMandateRepositoryPort}
import wvlet.airframe._

trait DDMandateBindedAdapters {
  val ddMandateBindDesign = newDesign
    .bind[BankAccountPort].toInstance(new FakeBankAccountAdapter())
    .bind[DDMandateContractPort].toInstance(new FakeDDMandateContractAdapter() )
    .bind[CreditorPort].toInstance(new FakeCreditorAdapter() )
    .bind[DDMandateRepositoryPort].toInstance(new FakeDDMandateRepositoryAdapter() )


}
