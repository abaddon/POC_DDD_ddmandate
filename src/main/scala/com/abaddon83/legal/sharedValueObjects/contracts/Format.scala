package com.abaddon83.legal.sharedValueObjects.contracts

sealed trait Format

case object PDF extends Format
case object DOC extends Format
case object JPG extends Format
