package com.abaddon83.legal.ddMandates

import com.abaddon83.legal.ddMandates.adapters.CreditorAdapters.fake.FakeCreditorAdapter
import com.abaddon83.legal.ddMandates.adapters.bankAccountAdapters.fake.FakeBankAccountAdapter
import com.abaddon83.legal.ddMandates.adapters.contractAdapters.fake.FakeContractAdapter
import com.abaddon83.legal.ddMandates.ports.{BankAccountPort, ContractPort, CreditorPort}
import wvlet.airframe._

trait DDMandateBindedAdapters {
  private val bindDesign = newDesign
    .bind[BankAccountPort].toInstance(new FakeBankAccountAdapter())
    .bind[ContractPort].toInstance(new FakeContractAdapter() )
    .bind[CreditorPort].toInstance(new FakeCreditorAdapter() )

  def getDDMandateBind() = {
    bindDesign
  }
}
