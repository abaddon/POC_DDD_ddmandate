package com.abaddon83.legal.domainModel.ddMandates


sealed trait Status
case object DRAFT extends Status
case object NOACCEPTED extends Status
case object ACCEPTED extends Status
case object CANCELED extends Status

