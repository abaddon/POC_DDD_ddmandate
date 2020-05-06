package com.abaddon83.shared.akkaHttp.messages

case class ErrorMessage(instance: String, message: String, exceptionType: String, errorCode: Int)
