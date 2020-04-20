package com.abaddon83.legal.domainModel.ddMandates


sealed trait DDMandateType
case object Financial extends DDMandateType
case object Core extends DDMandateType

