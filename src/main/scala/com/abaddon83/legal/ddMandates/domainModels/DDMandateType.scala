package com.abaddon83.legal.ddMandates.domainModels

sealed trait DDMandateType
case object Financial extends DDMandateType
case object Core extends DDMandateType

