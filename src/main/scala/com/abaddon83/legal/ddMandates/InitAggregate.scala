package com.abaddon83.legal.ddMandates

import com.abaddon83.legal.contracts.adapters.contractAdapters.akkaHttp.ContractRoutes
import com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp.DDMandateRoutes

class InitAggregate extends DDMandateBindedAdapters{

  getDDMandateBind().build[DDMandateRoutes]{ ddMandateRoutes: DDMandateRoutes =>
    ddMandateRoutes.getRoute()
  }
}
